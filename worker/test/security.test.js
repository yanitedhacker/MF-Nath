import test from "node:test";
import assert from "node:assert/strict";
import { API_KEY_HEADER, isAuthorized, wantsStream } from "../src/security.js";

test("public /chat when no API key is configured", () => {
  const request = new Request("https://doomsy.example/chat", { method: "POST" });
  assert.equal(isAuthorized(request, undefined), true);
  assert.equal(isAuthorized(request, ""), true);
});

test("matching X-Doomsy-Key is accepted", () => {
  const request = new Request("https://doomsy.example/chat", {
    method: "POST",
    headers: { [API_KEY_HEADER]: "secret-ledger" },
  });
  assert.equal(isAuthorized(request, "secret-ledger"), true);
});

test("missing or mismatched key is rejected when auth is on", () => {
  const missing = new Request("https://doomsy.example/chat", { method: "POST" });
  const wrong = new Request("https://doomsy.example/chat", {
    method: "POST",
    headers: { [API_KEY_HEADER]: "nope" },
  });
  assert.equal(isAuthorized(missing, "secret-ledger"), false);
  assert.equal(isAuthorized(wrong, "secret-ledger"), false);
});

test("Accept text/event-stream opts into token streaming", () => {
  assert.equal(wantsStream("text/event-stream, application/json"), true);
  assert.equal(wantsStream("application/json"), false);
  assert.equal(wantsStream(null), false);
});
