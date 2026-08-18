export const API_KEY_HEADER = "X-Doomsy-Key";

export function isAuthorized(request, expectedKey) {
  if (!expectedKey) {
    return true;
  }
  const provided = request.headers.get(API_KEY_HEADER) || "";
  return provided === expectedKey;
}

export function wantsStream(acceptHeader) {
  return (acceptHeader || "").toLowerCase().includes("text/event-stream");
}
