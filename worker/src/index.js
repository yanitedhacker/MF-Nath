import { DOOMSY_SYSTEM_PROMPT } from "./prompt.js";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type",
};

export default {
  async fetch(request, env) {
    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: corsHeaders });
    }

    const url = new URL(request.url);
    if (url.pathname !== "/chat") {
      return json({ error: "not_found" }, 404);
    }

    if (request.method !== "POST") {
      return json({ error: "method_not_allowed" }, 405);
    }

    let body;
    try {
      body = await request.json();
    } catch {
      return json({ error: "invalid_json" }, 400);
    }

    const message = typeof body.message === "string" ? body.message.trim() : "";
    const history = Array.isArray(body.history) ? body.history : [];

    if (!message) {
      return json({ error: "message_required" }, 400);
    }

    const messages = [
      { role: "system", content: DOOMSY_SYSTEM_PROMPT },
      ...toCloudflareHistory(history),
      { role: "user", content: message.slice(0, 2000) },
    ];

    try {
      const result = await env.AI.run(env.DOOMSY_MODEL, {
        messages,
        max_tokens: 160,
        temperature: 0.65,
        top_p: 0.9,
      });

      const reply = normalizeReply(result);
      if (!reply) {
        return json(
          {
            error: "empty_model_reply",
            detail: summarizeShape(result),
            model: env.DOOMSY_MODEL,
          },
          502,
        );
      }

      return json({
        reply,
        model: env.DOOMSY_MODEL,
        source: "cloudflare-workers-ai",
      });
    } catch (error) {
      return json(
        {
          error: "workers_ai_failed",
          detail: error instanceof Error ? error.message : "unknown_error",
        },
        502,
      );
    }
  },
};

function toCloudflareHistory(history) {
  return history
    .slice(-6)
    .flatMap((exchange) => {
      const user = typeof exchange?.user === "string" ? exchange.user.trim() : "";
      const assistant = typeof exchange?.assistant === "string" ? exchange.assistant.trim() : "";
      const items = [];
      if (user) {
        items.push({ role: "user", content: user.slice(0, 1200) });
      }
      if (assistant) {
        items.push({ role: "assistant", content: assistant.slice(0, 1200) });
      }
      return items;
    });
}

function normalizeReply(result) {
  if (!result) {
    return "";
  }

  if (typeof result === "string") {
    return clean(result);
  }

  if (typeof result.response === "string") {
    return clean(result.response);
  }

  if (typeof result.result === "string") {
    return clean(result.result);
  }

  if (typeof result.result?.response === "string") {
    return clean(result.result.response);
  }

  const choice = result.choices?.[0];
  if (typeof choice?.message?.content === "string") {
    return clean(choice.message.content);
  }

  if (Array.isArray(choice?.message?.content)) {
    return clean(
      choice.message.content
        .map((part) => (typeof part === "string" ? part : part?.text || ""))
        .join(" "),
    );
  }

  const nestedChoice = result.result?.choices?.[0];
  if (typeof nestedChoice?.message?.content === "string") {
    return clean(nestedChoice.message.content);
  }

  if (Array.isArray(nestedChoice?.message?.content)) {
    return clean(
      nestedChoice.message.content
        .map((part) => (typeof part === "string" ? part : part?.text || ""))
        .join(" "),
    );
  }

  if (Array.isArray(result.result?.output)) {
    return clean(
      result.result.output
        .map((item) => item?.content?.map?.((part) => part?.text || "").join(" ") || "")
        .join(" "),
    );
  }

  if (Array.isArray(result.output)) {
    return clean(
      result.output
        .map((item) => item?.content?.map?.((part) => part?.text || "").join(" ") || "")
        .join(" "),
    );
  }

  return "";
}

function clean(text) {
  return text.replace(/\s+/g, " ").trim();
}

function summarizeShape(value) {
  if (value == null) {
    return "null";
  }

  if (typeof value !== "object") {
    return typeof value;
  }

  return JSON.stringify(
    Object.fromEntries(
      Object.entries(value).map(([key, child]) => [
        key,
        Array.isArray(child) ? `array(${child.length})` : typeof child,
      ]),
    ),
  );
}

function json(payload, status = 200) {
  return new Response(JSON.stringify(payload), {
    status,
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      ...corsHeaders,
    },
  });
}
