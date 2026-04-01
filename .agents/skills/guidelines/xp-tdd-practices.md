---
name: xp-tdd-practices
description: TDD workflow with Red-Green-Refactor cycle, Transformation Priority Premise, and Inside-Out/Outside-In development strategies. Essential for Spring Boot development.
---

# XP TDD Practices

Test-Driven Development practices following XP (Extreme Programming) principles.

## The 5-Step TDD Cycle

### Step 1: Write a Failing Test (RED)
- Write a minimal test that describes the behavior you want
- The test must compile and fail for the right reason
- Do NOT write production code yet

### Step 2: Run the Test (Verify Failure)
- Confirm the test fails with the expected error
- The failure message should guide your implementation
- If it fails for wrong reasons, fix the test

### Step 3: Write Minimal Code to Pass (GREEN)
- Write only the code needed to make the test pass
- Do NOT optimize, refactor, or add features
- Focus on making the test green quickly

### Step 4: Run All Tests (Verify Success)
- Confirm all tests pass
- If any test fails, fix immediately before continuing
- This validates your implementation doesn't break existing functionality

### Step 5: Refactor (REFACTOR)
- Clean up code while keeping tests green
- Remove duplication
- Improve naming
- Apply design patterns
- Return to Step 1

## Transformation Priority Premise (TPP)

Apply transformations in this priority order (simpler to more complex):

| Priority | Transformation | Example |
|----------|---------------|---------|
| 1 | ( ) → nil | Return null instead of object |
| 2 | nil → constant | Return hardcoded value |
| 3 | constant → constant+ | Add more to constant |
| 4 | constant → variable | Introduce variable |
| 5 | stale variable → explicit | Split implicit value |
| 6 | constant array → array | Add elements |
| 7 | array → container | Add structure |
| 8 | statements to tail | Append code |
| 9 | recursion to loop | Iterate instead of recurse |
| 10 | if → while | Use loop instead of condition |
| 11 | expressions to statement | Make side effect explicit |

**Rule:** Prefer transformations that change behavior minimally.

## Development Strategies

### Inside-Out (Classical TDD)

```
Domain/Entity → Service/UseCase → Controller
```

1. Start with domain models and business rules
2. Build services that use those models
3. Add controllers at the top

**Best for:**
- Complex business logic
- Domain-driven design
- When you understand the domain well

### Outside-In (London TDD / Mockist)

```
Controller → Service → Domain
```

1. Start with the controller/endpoint
2. Use mocks to define interfaces
3. Implement the inside layers last

**Best for:**
- API development
- When requirements are UI-driven
- When you need to discover collaborators

## Common TDD Mistakes

### 1. Writing Too Much Test First
- Write one assertion or one behavior at a time
- Resist the urge to plan future tests

### 2. Writing Implementation Before Test
- Test must fail first
- If test passes without implementation, you're not doing TDD

### 3. Making Tests Too Broad
- Each test should test one thing
- Avoid massive test methods with multiple assertions

### 4. Testing Implementation Details
- Test behavior, not how it's implemented
- Refactoring should not break tests

### 5. Not Running Tests Frequently
- Run tests after every small change
- Long gaps lead to debugging nightmares

### 6. Ignoring Failing Tests
- A failing test is a compass pointing to what needs fixing
- Never leave the build red

### 7. Skipping the Refactor Phase
- Refactoring is not optional
- Technical debt accumulates quickly without it

## When to Write Tests

### Before Writing Code (TDD)
- New features
- Bug fixes
- Refactoring existing code

### After Writing Code (Traditional)
- Exploratory testing
- Learning new APIs
- Spike solutions (then delete)

## Test Naming Convention

```
[MethodName]_[Scenario]_[ExpectedResult]

Examples:
- findById_returnsUser_whenUserExists
- save_throwsException_whenEmailIsInvalid
- create_withValidData_returnsCreatedUser
```

## Integration with Spring Boot

See `springboot-tdd` skill for:
- JUnit 5 + Mockito patterns
- MockMvc web layer tests
- @DataJpaTest persistence tests
- Testcontainers integration

## Non-Negotiable Rules

1. **No production code without a failing test first**
2. **Never leave tests failing**
3. **Refactor only with tests green**
4. **Keep tests fast (< 100ms each)**

---

**Related Guidelines:**
- `testing-standards` - Test structure, FIRST principles
- `springboot-tdd` - Spring Boot specific patterns
