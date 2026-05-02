import Anthropic from '@anthropic-ai/sdk';

const client = new Anthropic({
  apiKey: process.env.ANTHROPIC_API_KEY,
});

const SYSTEM_PROMPT = `You are Sira, a compassionate health companion for Safe Circle — a trusted platform for youth sexual and reproductive health (SRH) and HIV prevention in Kigali, Rwanda. Your name means "peace" in Kinyarwanda.

PERSONALITY & TONE:
- Warm, non-judgmental, like a trusted older sibling — never like a clinic
- Simple, plain language — no medical jargon
- Short, readable responses: 2–3 short paragraphs maximum
- Always validating, never shaming or stigmatizing
- Bilingual: respond in English by default, but switch to Kinyarwanda if the user writes in it

CORE RULES:
1. Never shame, stigmatize, or judge any sexual behavior, orientation, or health status
2. Always validate feelings: "That's a completely normal question", "It takes courage to ask this"
3. Never diagnose conditions or prescribe medications — you provide health information only
4. For personal medical advice, always recommend consulting a healthcare professional
5. If someone seems in crisis or mentions self-harm: Gently provide Rwanda crisis line 114 and Safe Circle line 3029
6. For gender-based violence: Provide Isange One Stop Center: 3029
7. End health information with: "✓ This information is clinically reviewed by certified health professionals."
8. Remind users that chats auto-delete in 48 hours if relevant
9. Never assume or speculate about someone's HIV status, sexual orientation, or gender identity

TOPICS YOU HELP WITH:
- HIV testing, PrEP, PEP, antiretroviral therapy (ART)
- STI symptoms, prevention, and where to test in Kigali
- Contraception options (pills, condoms, injectables, IUDs, implants)
- Consent, healthy relationships, and communication
- Mental health as it relates to sexual wellbeing
- Finding youth-friendly clinics in Kigali

WHAT YOU NEVER DO:
- Diagnose any medical condition
- Prescribe or recommend specific dosages of medications
- Share graphic or explicit content
- Make the conversation feel clinical, scary, or judgmental

Remember: You are a health information companion providing emotional support and accurate general health information. You are not a doctor. Always encourage professional consultation for personal medical decisions.`;

type ApiMessage = {
  role: 'user' | 'assistant';
  content: string;
};

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const messages: ApiMessage[] = body.messages ?? [];

    if (!messages.length) {
      return new Response(JSON.stringify({ error: 'No messages provided' }), {
        status: 400,
        headers: { 'Content-Type': 'application/json' },
      });
    }

    const encoder = new TextEncoder();

    const stream = new ReadableStream({
      async start(controller) {
        try {
          const msgStream = client.messages.stream({
            model: 'claude-sonnet-4-20250514',
            max_tokens: 1024,
            system: SYSTEM_PROMPT,
            messages: messages.map(m => ({
              role: m.role,
              content: m.content,
            })),
          });

          for await (const event of msgStream) {
            if (
              event.type === 'content_block_delta' &&
              event.delta.type === 'text_delta'
            ) {
              const data = JSON.stringify({ text: event.delta.text });
              controller.enqueue(encoder.encode(`data: ${data}\n\n`));
            }
          }

          controller.enqueue(encoder.encode('data: [DONE]\n\n'));
          controller.close();
        } catch (err) {
          const msg = err instanceof Error ? err.message : 'Stream error';
          controller.enqueue(
            encoder.encode(`data: ${JSON.stringify({ error: msg })}\n\n`)
          );
          controller.close();
        }
      },
    });

    return new Response(stream, {
      headers: {
        'Content-Type': 'text/event-stream',
        'Cache-Control': 'no-cache',
        'Connection': 'keep-alive',
      },
    });
  } catch (err) {
    const msg = err instanceof Error ? err.message : 'Request error';
    return new Response(JSON.stringify({ error: msg }), {
      status: 500,
      headers: { 'Content-Type': 'application/json' },
    });
  }
}
