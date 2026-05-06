# Vibe Coding Roadmap

- Roadmap: https://roadmap.sh/vibe-coding

## 1. What is Vibe Coding?

- 1.1 The Vibe Coder Mindset

## 2. AI Assisted Coding Tools

- 2.1 Claude Code
- 2.2 Gemini
- 2.3 ChatGPT
- 2.4 Cursor
- 2.5 Windsurf
- 2.6 Replit

### 2.7 Frontend-Focused

- 2.7.1 v0
- 2.7.2 Lovable

## 3. Plan Before You Code

- 3.1 Plan what you need to develop (MVP, Different Phases)
- 3.2 Work step by step rather than trying to build everything at once
- 3.3 Illustrate AI with examples (mockups, code samples, images)

## 4. Tech Stack and Coding

- 4.1 Pick a popular tech stack rather than new/niche ones
- 4.2 If you have style/coding preferences, document them for AI
- 4.3 Ask AI to keep the code modular and aim for smaller modules/files
- 4.4 Regularly ask the AI to review and refactor the codebase
- 4.5 Use skills created by others

## 5. Prompting Best Practices

- 5.1 Ask for one task at a time rather than five different items
- 5.2 Be specific about what you want, rather than high-level, vague instructions
- 5.3 Based on your previous coding sessions, tell AI what NOT to do
- 5.4 Give AI mockups, reference files and material that can help it
- 5.5 Use "act as" framing when helpful (e.g. act as a UX researcher)
- 5.6 Regularly update your context document (e.g. CLAUDE.md)
- 5.7 Explicitly tell AI to "think" or "brainstorm" before complex problems

## 6. Context

- 6.1 Leverage long context window when available and necessary
- 6.2 If AI fails after 3 prompts, stop, and start a fresh chat
- 6.3 For unrelated tasks, proactively clean and start new sessions
- 6.4 Ask AI to use subagents, if possible

## 7. Debugging

- 7.1 Prompt the error message and let AI do the rest
- 7.2 If errors persist, ask AI to create a list of possible causes
- 7.3 Tell AI to add logs to find the error faster
- 7.4 Install and ask AI to use MCP (e.g. Playwright for browser), when possible

## 8. Master Version Control

- 8.1 Use `git commit` regularly (e.g. after every successful AI task)
- 8.2 Start each new feature with a clean Git slate
- 8.3 If you need to revert, use Git rather than AI native revert functionality
- 8.4 Ask AI to handle your Git and GitHub CLI tasks

## 9. Testing

- 9.1 Ask AI to write tests (E2E tests can help build a stable product)
- 9.2 Consider Test-driven development (TDD)
- 9.3 When you find a bug, ask AI to write a breaking test and then fix
- 9.4 Once tests are in place, refactor regularly

## 10. Security Best Practices

- 10.1 Explicitly ask AI to perform a security audit of the application
- 10.2 Never hardcode or credentials; use env variables instead
