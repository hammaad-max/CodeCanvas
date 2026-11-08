package com.hammaad.codecanvas.repository;

import com.hammaad.codecanvas.model.Question;

import java.util.List;

/**
 * Additional ICSE questions (IDs 129-160) that bring every ICSE unit to 10 questions, matching AP CS.
 * Code-output answers were checked by running the snippets with Java.
 */
final class IcseExtraQuestions {

    private static final String ICSE = "ICSE";

    private IcseExtraQuestions() {
    }

    private static Question q(long id, String text, String a, String b, String c, String d,
                              String answer, String explanation, String topic) {
        return new Question(id, text, a, b, c, d, answer, explanation, topic, ICSE);
    }

    static List<Question> all() {
        String oop = "OOP & Java Fundamentals";
        String ops = "Operators & Expressions";
        String cond = "Conditional Branching & Switch";
        String loops = "Iterative Constructs & Loops";
        String nums = "Classic Number Programs";
        String str = "Character & String Handling";
        String meth = "User-Defined Methods & Overloading";
        String arr = "Arrays, Searching & Sorting";
        return List.of(
                // ---------------- OOP & Java Fundamentals
                q(129, "Which statement correctly creates an object of a class named Book?",
                        "Book b = new Book();", "Book b = Book();", "new Book b();", "Book b = create Book();",
                        "Book b = new Book();",
                        "Objects are created with the new keyword followed by a call to a constructor.", oop),
                q(130, "Which description fits a constructor in Java?",
                        "A method that must return void", "A member with the same name as the class and no return type, called automatically when an object is created",
                        "A static method that creates classes", "Any method that initialises a local variable",
                        "A member with the same name as the class and no return type, called automatically when an object is created",
                        "A constructor initialises a new object. It has the class's name and no return type, not even void.", oop),
                q(131, "When does Java automatically supply a default (no-argument) constructor?",
                        "Always, for every class", "Only when the class defines no constructor at all",
                        "Only when a parameterised constructor exists", "Only for public classes",
                        "Only when the class defines no constructor at all",
                        "As soon as you write any constructor yourself, Java no longer provides the default one.", oop),
                q(132, "Which of the following is platform dependent?",
                        "Java bytecode", "A .class file", "The Java Virtual Machine (JVM)", "Java source code",
                        "The Java Virtual Machine (JVM)",
                        "Bytecode runs anywhere, but each operating system needs its own JVM to interpret it.", oop),

                // ---------------- Operators & Expressions
                q(133, "What is the value of Math.round(-2.5)?",
                        "-3", "-2", "-2.5", "-3.0",
                        "-2",
                        "Math.round(x) behaves like Math.floor(x + 0.5), so -2.5 becomes floor(-2.0) = -2. The result is a long.", ops),
                q(134, "What is the data type of the value returned by Math.pow(2, 3)?",
                        "int", "long", "double", "float",
                        "double",
                        "Math.pow always returns a double (8.0 here); assigning it to an int needs an explicit cast.", ops),
                q(135, "What is the value of (double) (17 / 5)?",
                        "3.4", "3.0", "3", "4.0",
                        "3.0",
                        "The integer division 17 / 5 happens first (giving 3), and only then is 3 cast to 3.0.", ops),
                q(136, "If int a = 10, b = 3; what does System.out.println(a / b + a % b); print?",
                        "4", "3.33", "13", "31",
                        "4",
                        "a / b is 3 (integer division) and a % b is 1, so the sum is 4. Both are ints, so no text joining happens.", ops),

                // ---------------- Conditional Branching & Switch
                q(137, "int marks = 62; char g = (marks >= 75) ? 'A' : (marks >= 60) ? 'B' : 'C'; What is the value of g?",
                        "A", "B", "C", "It does not compile",
                        "B",
                        "62 >= 75 is false, so the second ternary is evaluated; 62 >= 60 is true, giving 'B'.", cond),
                q(138, "Which statement about the && operator is TRUE?",
                        "Both operands are always evaluated", "If the left operand is false, the right operand is not evaluated",
                        "It returns an int", "It has lower precedence than ||",
                        "If the left operand is false, the right operand is not evaluated",
                        "&& short-circuits: false && anything is false, so Java skips the right-hand side.", cond),
                q(139, "Which of the following can NOT be used as a case label in a switch statement?",
                        "case 5:", "case 'A':", "case \"YES\":", "case x > 5:",
                        "case x > 5:",
                        "Case labels must be constant values; a relational expression like x > 5 is not allowed.", cond),
                q(140, "What is printed? int n = 3; switch (n) { case 1: System.out.print(\"One \"); case 3: System.out.print(\"Three \"); case 4: System.out.print(\"Four \"); break; default: System.out.print(\"None\"); }",
                        "Three", "Three Four", "Three Four None", "None",
                        "Three Four",
                        "Execution starts at case 3 and falls through into case 4 because there is no break, then stops at the break.", cond),

                // ---------------- Iterative Constructs & Loops
                q(141, "What is the value of sum after: int sum = 0; for (int i = 1; i <= 10; i += 3) sum += i;",
                        "22", "12", "55", "15",
                        "22",
                        "i takes the values 1, 4, 7 and 10, and 1 + 4 + 7 + 10 = 22.", loops),
                q(142, "How many times does the body run? int k = 1; while (k < 50) k = k * 3;",
                        "3", "4", "5", "It never stops",
                        "4",
                        "k goes 1, 3, 9, 27, 81. The body runs for k = 1, 3, 9 and 27; when k is 81 the condition is false.", loops),
                q(143, "What is printed? for (int i = 1; i <= 5; i++) { if (i == 3) continue; System.out.print(i); }",
                        "12", "1245", "12345", "345",
                        "1245",
                        "continue skips the rest of the pass when i is 3, so only 3 is missing from the output.", loops),
                q(144, "How many stars are printed in total? for (int i = 1; i <= 3; i++) { for (int j = 1; j <= i; j++) System.out.print(\"*\"); System.out.println(); }",
                        "3", "5", "6", "9",
                        "6",
                        "The inner loop runs 1, 2 and 3 times for the three rows: 1 + 2 + 3 = 6 stars.", loops),

                // ---------------- Classic Number Programs
                q(145, "For n = 1234, what does this compute? int s = 0; while (n > 0) { s += n % 10; n /= 10; }",
                        "10, the sum of the digits", "4321, the reverse", "24, the product of the digits", "4, the number of digits",
                        "10, the sum of the digits",
                        "Each pass adds the last digit (4, 3, 2, 1) to s and removes it, so s = 10.", nums),
                q(146, "A Niven (Harshad) number is divisible by the sum of its digits. Which of these is a Niven number?",
                        "121", "123", "125", "126",
                        "126",
                        "1 + 2 + 6 = 9 and 126 / 9 = 14 exactly. The others leave a remainder when divided by their digit sums.", nums),
                q(147, "A Neon number equals the sum of the digits of its square. Which of these is a Neon number?",
                        "8", "9", "10", "12",
                        "9",
                        "9 x 9 = 81 and 8 + 1 = 9.", nums),
                q(148, "Why is 1 NOT a prime number?",
                        "Because it is odd", "Because it has only one factor, while a prime has exactly two",
                        "Because it is less than 2 digits", "Because it is a perfect number",
                        "Because it has only one factor, while a prime has exactly two",
                        "A prime has exactly two distinct factors, 1 and itself. 1 has only a single factor.", nums),

                // ---------------- Character & String Handling
                q(149, "What is the value of \"COMPUTER\".indexOf('U')?",
                        "3", "4", "5", "-1",
                        "4",
                        "Indexes start at 0: C=0, O=1, M=2, P=3, U=4.", str),
                q(150, "What happens when \"Hello\".charAt(5) is executed?",
                        "It returns 'o'", "It returns an empty character", "It throws StringIndexOutOfBoundsException", "It returns -1",
                        "It throws StringIndexOutOfBoundsException",
                        "\"Hello\" has indexes 0 to 4, so index 5 is out of range.", str),
                q(151, "What does System.out.println(Character.toUpperCase('b') + 1); print?",
                        "C", "'C'", "66", "67",
                        "67",
                        "toUpperCase gives 'B' (ASCII 66). Adding an int to a char gives an int, so 66 + 1 = 67 is printed.", str),
                q(152, "What is printed? String s = \"java\"; s.toUpperCase(); System.out.println(s);",
                        "java", "JAVA", "Java", "It does not compile",
                        "java",
                        "Strings are immutable. toUpperCase returns a new String, which is discarded because it is not assigned.", str),

                // ---------------- User-Defined Methods & Overloading
                q(153, "Which pair of methods is a VALID example of method overloading?",
                        "int sum(int a, int b) and int sum(int a, int b, int c)", "int sum(int a) and double sum(int a)",
                        "void show(int x) and void show(int y)", "int area(int s) and int area(int side)",
                        "int sum(int a, int b) and int sum(int a, int b, int c)",
                        "Overloads must differ in their parameter lists. Changing only the return type or parameter names is not enough.", meth),
                q(154, "In the statement result = square(7); what is 7?",
                        "A formal parameter", "An actual parameter (argument)", "The return value", "The method signature",
                        "An actual parameter (argument)",
                        "Values written in the call are actual parameters; the variables in the method header are formal parameters.", meth),
                q(155, "What is printed? static void change(int[] a) { a[0] = 99; }  ...  int[] arr = {1, 2}; change(arr); System.out.println(arr[0]);",
                        "1", "2", "99", "0",
                        "99",
                        "The method receives a copy of the reference to the same array, so changing a[0] changes arr[0].", meth),
                q(156, "Which statement about the return statement is correct?",
                        "It can return several values at once", "It sends a value back to the caller and ends the method immediately",
                        "It is required in every void method", "It prints a value on the screen",
                        "It sends a value back to the caller and ends the method immediately",
                        "return ends the method; in a non-void method it also supplies the result to the calling statement.", meth),

                // ---------------- Arrays, Searching & Sorting
                q(157, "What is the index of the last element of int[] a = new int[8];?",
                        "8", "7", "9", "0",
                        "7",
                        "Indexes run from 0 to length - 1, so the last index is 8 - 1 = 7.", arr),
                q(158, "What value does every element of int[] a = new int[5]; hold right after creation?",
                        "0", "null", "Garbage values", "1",
                        "0",
                        "Numeric array elements are automatically initialised to 0.", arr),
                q(159, "What happens in each pass of selection sort (ascending order)?",
                        "Adjacent elements are swapped if they are out of order",
                        "The smallest element of the unsorted part is swapped into the next position at the front",
                        "The array is split into two halves", "The largest element is removed from the array",
                        "The smallest element of the unsorted part is swapped into the next position at the front",
                        "Selection sort selects the minimum of the unsorted part each pass; swapping adjacent elements describes bubble sort.", arr),
                q(160, "Binary search for any value in {3, 8, 15, 23, 42, 57, 61}: which element is compared first?",
                        "3", "15", "23", "42",
                        "23",
                        "low = 0 and high = 6, so mid = (0 + 6) / 2 = 3, and the element at index 3 is 23.", arr)
        );
    }
}
