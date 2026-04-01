---
name: action-tdd
description: Enforces TDD cycle when being skipped. Use when developer attempts to write production code without a failing test first.
---

# Action: Enforce TDD Cycle

Use this when you detect someone (including yourself) attempting to skip the TDD cycle.

## Trigger Conditions

Execute this action when:
- Someone writes production code without a test
- Someone suggests "I'll write tests later"
- A test is not failing at the start of implementation
- Refactoring is attempted without test coverage

## Enforcement Steps

### 1. Stop and Identify

```
STOP: No production code will be written without a failing test first.
```

Identify the behavior that needs to be implemented:
- What should the code do?
- What are the inputs and outputs?
- What are the edge cases?

### 2. Write the Failing Test First

Create a minimal test that describes the desired behavior:

```java
@Test
void [MethodName]_[Scenario]_[ExpectedResult]() {
    // Arrange - Set up test data
    // ...
    
    // Act - Call the method that doesn't exist yet
    // This will cause a compilation error - that's expected!
    
    // Assert - Describe expected behavior
    assertThat(result).isEqualTo(expected);
}
```

**Important:** The test MUST fail for the right reason. If it fails because the method doesn't exist, that's correct. If it passes without implementation, you skipped TDD.

### 3. Make the Test Compile (Minimal)

Add only the method signature needed:

```java
public class UsuarioService {
    public Optional<Usuario> findById(long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
```

### 4. Run the Test

Verify it fails with the expected error:
- `UnsupportedOperationException` = Test is correct
- Test passes = You have implementation code somewhere (delete it)

### 5. Implement Minimal Code

Write only what's needed to make the test pass:

```java
public Optional<Usuario> findById(long id) {
    return repository.findById(id);
}
```

### 6. Verify All Tests Pass

Run the full test suite. All tests must pass before continuing.

### 7. Refactor

Now you can clean up the code while keeping tests green.

## Pre-Implementation Checklist

Before writing ANY production code:

- [ ] Is there a failing test that describes the desired behavior?
- [ ] Does the test fail for the right reason?
- [ ] Will the implementation make this specific test pass?
- [ ] Will existing tests still pass?

## Non-Negotiable Rules

1. **Never write production code without a failing test first**
2. **Never leave the build red**
3. **Refactor only with tests green**
4. **Tests must be faster than 100ms (unit tests)**

## Common Excuses (and Responses)

| Excuse | Response |
|--------|----------|
| "It's just a quick fix" | Quick fixes become permanent code. Test it. |
| "I'll write tests later" | Later never comes. Test first. |
| "The code is too simple to test" | If it's too simple to test, it's too simple to break. |
| "Tests slow me down" | Tests speed up debugging by 10x. |
| "I don't know how to test this" | That's a learning opportunity, not an exception. |

## Integration with Development Workflow

This action is automatically invoked when:
- Starting a new feature
- Fixing a bug
- Refactoring existing code
- Adding new endpoints

Use `/task-validate` to verify the full cycle completed correctly.

## Related

- `xp-tdd-practices` - Detailed TDD cycle and TPP
- `testing-standards` - Test structure and naming
- `task-validate` - Full validation after implementation
