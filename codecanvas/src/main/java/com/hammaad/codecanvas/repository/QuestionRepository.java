package com.hammaad.codecanvas.repository;

import com.hammaad.codecanvas.model.Question;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class QuestionRepository {

    private final List<Question> questions = new ArrayList<>();

    public QuestionRepository() {

        // ==========================================
        //  TOPIC 1: Variables & Data Types (IDs 1-10)
        // ==========================================
        questions.add(new Question(
                1L,
                "Which data type is used to store whole numbers in Java?",
                "double", "int", "boolean", "String",
                "int",
                "The int data type is used to store whole numbers such as 10, 25, or 100.",
                "Variables & Data Types", "AP CS"
        ));
        questions.add(new Question(
                2L,
                "Which statement correctly declares an integer variable called age with the value 18?",
                "age int = 18;", "int age = 18;", "integer age = 18;", "18 = int age;",
                "int age = 18;",
                "In Java, the data type comes first, followed by the variable name and then the assigned value.",
                "Variables & Data Types", "AP CS"
        ));
        questions.add(new Question(
                3L,
                "Which data type should be used for a decimal value such as 87.5?",
                "int", "boolean", "double", "char",
                "double",
                "The double data type is commonly used to store decimal numbers.",
                "Variables & Data Types", "AP CS"
        ));
        questions.add(new Question(
                4L,
                "Which data type is used to store a single character in Java?",
                "String", "char", "character", "boolean",
                "char",
                "The char data type stores a single character, such as 'A' or '7'.",
                "Variables & Data Types", "AP CS"
        ));
        questions.add(new Question(
                5L,
                "Which data type stores either true or false?",
                "int", "double", "boolean", "char",
                "boolean",
                "The boolean data type can contain only two values: true or false.",
                "Variables & Data Types", "AP CS"
        ));
        questions.add(new Question(
                6L,
                "Which declaration correctly creates a double variable called price?",
                "double price = 19.99;", "int price = 19.99;", "price double = 19.99;", "double = price 19.99;",
                "double price = 19.99;",
                "A double can store decimal values. The correct syntax is the data type followed by the variable name and assigned value.",
                "Variables & Data Types", "AP CS"
        ));
        questions.add(new Question(
                7L,
                "Which declaration correctly creates a String variable?",
                "String name = \"Josh\";", "string name = Josh;", "String = name \"Josh\";", "name String = \"Josh\";",
                "String name = \"Josh\";",
                "String is the Java class used to represent text. Text values are written inside double quotation marks.",
                "Variables & Data Types", "AP CS"
        ));
        questions.add(new Question(
                8L,
                "What value is stored in the variable age after this statement?\nint age = 18;",
                "0", "18", "age", "int",
                "18",
                "The value 18 is assigned to the variable age.",
                "Variables & Data Types", "AP CS"
        ));
        questions.add(new Question(
                9L,
                "Which data type would be most appropriate for storing a student's test score such as 87.5?",
                "boolean", "char", "double", "int",
                "double",
                "A double is appropriate when a value may contain a decimal portion.",
                "Variables & Data Types", "AP CS"
        ));
        questions.add(new Question(
                10L,
                "Which statement changes the value of an existing int variable called score to 95?",
                "int score = 95;", "score = 95;", "score int = 95;", "change score to 95;",
                "score = 95;",
                "When score already exists as a variable, the assignment statement score = 95; changes its stored value.",
                "Variables & Data Types", "AP CS"
        ));


        // ==========================================
        //  TOPIC 2: Expressions & Assignment (IDs 11-20)
        // ==========================================
        questions.add(new Question(
                11L,
                "Which operator is used to add two values in Java?",
                "+", "-", "*", "/",
                "+",
                "The + operator performs addition when used with numeric values.",
                "Expressions & Assignment", "AP CS"
        ));
        questions.add(new Question(
                12L,
                "What is the value of x after this statement?\nint x = 5 + 3;",
                "2", "8", "15", "53",
                "8",
                "The expression 5 + 3 evaluates to 8, which is then assigned to x.",
                "Expressions & Assignment", "AP CS"
        ));
        questions.add(new Question(
                13L,
                "Which operator is used for multiplication in Java?",
                "x", "*", "%", "^",
                "*",
                "Java uses the * operator for multiplication.",
                "Expressions & Assignment", "AP CS"
        ));
        questions.add(new Question(
                14L,
                "What does the assignment operator = do?",
                "Compares two values for equality", "Assigns a value to a variable", "Adds two values", "Checks a condition",
                "Assigns a value to a variable",
                "The = operator assigns the value on its right to the variable on its left.",
                "Expressions & Assignment", "AP CS"
        ));
        questions.add(new Question(
                15L,
                "What is the value of result after:\nint result = 10 - 4;",
                "6", "14", "40", "104",
                "6",
                "The subtraction expression 10 - 4 evaluates to 6.",
                "Expressions & Assignment", "AP CS"
        ));
        questions.add(new Question(
                16L,
                "What is the result of integer division in Java for the expression 7 / 2?",
                "3.5", "3", "4", "3.0",
                "3",
                "When both operands in division are integers, Java performs integer division and truncates the decimal remainder, yielding 3.",
                "Expressions & Assignment", "AP CS"
        ));
        questions.add(new Question(
                17L,
                "What is the result of the modulus expression 17 % 5 in Java?",
                "3", "2", "3.4", "1",
                "2",
                "The % operator returns the integer remainder after division: 17 divided by 5 is 3 with a remainder of 2.",
                "Expressions & Assignment", "AP CS"
        ));
        questions.add(new Question(
                18L,
                "What is the value of result after:\nint result = 2 + 3 * 4;",
                "20", "14", "24", "18",
                "14",
                "Multiplication (*) has higher precedence than addition (+). 3 * 4 evaluates first to 12, then 2 + 12 = 14.",
                "Expressions & Assignment", "AP CS"
        ));
        questions.add(new Question(
                19L,
                "If int count = 5; what is the value of count after count += 3;?",
                "3", "5", "8", "15",
                "8",
                "The compound assignment count += 3 is equivalent to count = count + 3, resulting in 8.",
                "Expressions & Assignment", "AP CS"
        ));
        questions.add(new Question(
                20L,
                "What is the result of the cast expression (double) 5 / 2 in Java?",
                "2", "2.0", "2.5", "3.0",
                "2.5",
                "The cast (double) applies to 5, making it 5.0. 5.0 / 2 results in floating-point division yielding 2.5.",
                "Expressions & Assignment", "AP CS"
        ));


        // ==========================================
        //  TOPIC 3: Strings (IDs 21-30)
        // ==========================================
        questions.add(new Question(
                21L,
                "What index does the first character of a String have in Java?",
                "0", "1", "-1", "Depends on String length",
                "0",
                "Java strings are zero-indexed, meaning the very first character is located at index 0.",
                "Strings", "AP CS"
        ));
        questions.add(new Question(
                22L,
                "Given String word = \"Computer\"; what does word.length() return?",
                "7", "8", "9", "null",
                "8",
                "The length() method returns the total count of characters in the string, which is 8 for \"Computer\".",
                "Strings", "AP CS"
        ));
        questions.add(new Question(
                23L,
                "Given String s = \"Java\"; what is returned by s.substring(1, 3)?",
                "\"Jav\"", "\"av\"", "\"ava\"", "\"a\"",
                "\"av\"",
                "substring(start, end) includes index start (1 -> 'a') up to but excluding index end (3), giving \"av\".",
                "Strings", "AP CS"
        ));
        questions.add(new Question(
                24L,
                "Which method should be used to test if two String objects have the exact same text?",
                "==", ".equals()", ".compare()", ".same()",
                ".equals()",
                "The .equals() method compares the actual textual content of String objects, whereas == compares memory references.",
                "Strings", "AP CS"
        ));
        questions.add(new Question(
                25L,
                "Given String str = \"banana\"; what is the return value of str.indexOf(\"na\")?",
                "1", "2", "4", "-1",
                "2",
                "indexOf returns the index of the first occurrence of the substring. The first \"na\" begins at index 2.",
                "Strings", "AP CS"
        ));
        questions.add(new Question(
                26L,
                "What does str.indexOf(\"z\") return if \"z\" is not found in str?",
                "0", "-1", "false", "Throws an Exception",
                "-1",
                "When the target substring or character is not present in the String, indexOf() returns -1.",
                "Strings", "AP CS"
        ));
        questions.add(new Question(
                27L,
                "What is the result of \"Score: \" + 10 + 20 in Java?",
                "\"Score: 30\"", "\"Score: 1020\"", "\"Score: 10 20\"", "Compilation error",
                "\"Score: 1020\"",
                "Evaluation proceeds left-to-right. \"Score: \" + 10 becomes \"Score: 10\", then concatenating 20 yields \"Score: 1020\".",
                "Strings", "AP CS"
        ));
        questions.add(new Question(
                28L,
                "What property of String objects means their contents cannot be changed after creation?",
                "Inheritance", "Immutability", "Polymorphism", "Encapsulation",
                "Immutability",
                "Java strings are immutable; any method that appears to modify a String actually returns a new String object.",
                "Strings", "AP CS"
        ));
        questions.add(new Question(
                29L,
                "What escape sequence is used in Java string literals to produce a new line?",
                "\\t", "\\n", "\\r", "\\\"",
                "\\n",
                "The \\n escape character produces a newline break in console and text output.",
                "Strings", "AP CS"
        ));
        questions.add(new Question(
                30L,
                "Given String s = \"apple\"; what is returned by s.substring(2)?",
                "\"pp\"", "\"ple\"", "\"pple\"", "\"le\"",
                "\"ple\"",
                "When given a single parameter, substring(start) returns the substring from start to the end of the string.",
                "Strings", "AP CS"
        ));


        // ==========================================
        //  TOPIC 4: Selection (IDs 31-40)
        // ==========================================
        questions.add(new Question(
                31L,
                "Which operator is used to compare whether two primitive values are equal in Java?",
                "=", "==", "equals", "===",
                "==",
                "The == operator tests equality between two primitive values (such as integers or booleans).",
                "Selection", "AP CS"
        ));
        questions.add(new Question(
                32L,
                "What does the logical operator && represent in Java boolean expressions?",
                "Logical OR", "Logical NOT", "Logical AND", "Bitwise XOR",
                "Logical AND",
                "&& is the logical AND operator; it evaluates to true only if BOTH operands are true.",
                "Selection", "AP CS"
        ));
        questions.add(new Question(
                33L,
                "What does the expression !(5 > 3) evaluate to in Java?",
                "true", "false", "5", "3",
                "false",
                "5 > 3 is true. The logical NOT operator (!) inverts true to false.",
                "Selection", "AP CS"
        ));
        questions.add(new Question(
                34L,
                "Which statement executes code only when a condition evaluates to true?",
                "if statement", "while loop", "for loop", "return statement",
                "if statement",
                "The if statement evaluates a boolean condition and executes its block only if that condition is true.",
                "Selection", "AP CS"
        ));
        questions.add(new Question(
                35L,
                "What happens in short-circuit evaluation for the expression: false && (x / 0 == 1)?",
                "Throws an ArithmeticException", "Evaluates both sides and crashes", "Evaluates to false without checking the right operand", "Evaluates to true",
                "Evaluates to false without checking the right operand",
                "Because the left operand of && is false, the entire expression must be false, so Java skips evaluating the right side.",
                "Selection", "AP CS"
        ));
        questions.add(new Question(
                36L,
                "What is printed by the following code?\nint score = 85;\nif (score >= 90) { System.out.print(\"A\"); }\nelse if (score >= 80) { System.out.print(\"B\"); }\nelse { System.out.print(\"C\"); }",
                "A", "B", "BC", "ABC",
                "B",
                "Since score is 85, score >= 90 is false, but score >= 80 is true, so \"B\" prints and the else block is skipped.",
                "Selection", "AP CS"
        ));
        questions.add(new Question(
                37L,
                "Which logical operator evaluates to true if AT LEAST ONE of its operands is true?",
                "&&", "||", "!", "^",
                "||",
                "The logical OR operator (||) evaluates to true if either operand (or both) is true.",
                "Selection", "AP CS"
        ));
        questions.add(new Question(
                38L,
                "According to De Morgan's Laws, !(A && B) is equivalent to which expression?",
                "!A && !B", "!A || !B", "A || B", "!(A || B)",
                "!A || !B",
                "De Morgan's Laws state that negating an AND flips both operands with NOT and changes AND to OR: !A || !B.",
                "Selection", "AP CS"
        ));
        questions.add(new Question(
                39L,
                "What is the output of the following?\nint x = 10;\nif (x > 5) {\n    if (x < 15) { System.out.print(\"Inside\"); }\n}",
                "Inside", "Nothing", "x", "Compilation error",
                "Inside",
                "x (10) is greater than 5 and less than 15, so both nested if conditions are satisfied and \"Inside\" is printed.",
                "Selection", "AP CS"
        ));
        questions.add(new Question(
                40L,
                "Which operator checks if two values are NOT equal in Java?",
                "<>", "!=", "!==", "not",
                "!=",
                "The != operator returns true if the two values being compared are different.",
                "Selection", "AP CS"
        ));


        // ==========================================
        //  TOPIC 5: Iteration (IDs 41-50)
        // ==========================================
        questions.add(new Question(
                41L,
                "How many times will this loop execute?\nfor (int i = 0; i < 5; i++) { ... }",
                "4", "5", "6", "Infinite",
                "5",
                "i starts at 0 and increments: 0, 1, 2, 3, 4. The loop stops when i reaches 5, running exactly 5 times.",
                "Iteration", "AP CS"
        ));
        questions.add(new Question(
                42L,
                "What will be the value of sum after the loop?\nint sum = 0;\nfor (int i = 1; i <= 3; i++) { sum += i; }",
                "3", "5", "6", "7",
                "6",
                "The loop adds 1, then 2, then 3 to sum: 0 + 1 + 2 + 3 = 6.",
                "Iteration", "AP CS"
        ));
        questions.add(new Question(
                43L,
                "What is an infinite loop in Java?",
                "A loop that never terminates because its condition remains true", "A loop with 1,000,000 iterations", "A loop inside another loop", "A loop that throws an error",
                "A loop that never terminates because its condition remains true",
                "An infinite loop occurs when the loop condition never evaluates to false, causing execution to continue indefinitely.",
                "Iteration", "AP CS"
        ));
        questions.add(new Question(
                44L,
                "What is printed by the following code?\nint k = 1;\nwhile (k < 4) {\n    System.out.print(k + \" \");\n    k++;\n}",
                "1 2 3 ", "1 2 3 4 ", "2 3 4 ", "0 1 2 3 ",
                "1 2 3 ",
                "k starts at 1, prints 1 (k becomes 2), prints 2 (k becomes 3), prints 3 (k becomes 4). k < 4 is then false, so it stops.",
                "Iteration", "AP CS"
        ));
        questions.add(new Question(
                45L,
                "How many total times does the inner statement execute?\nfor (int i = 0; i < 3; i++) {\n    for (int j = 0; j < 4; j++) {\n        System.out.print(\"*\");\n    }\n}",
                "7", "12", "16", "9",
                "12",
                "The outer loop executes 3 times. For each outer iteration, the inner loop executes 4 times: 3 * 4 = 12 total times.",
                "Iteration", "AP CS"
        ));
        questions.add(new Question(
                46L,
                "What error describes a loop that executes one time too few or one time too many?",
                "Off-by-one error", "NullPointerException", "StackOverflowError", "Type mismatch",
                "Off-by-one error",
                "An off-by-one error commonly happens when using <= instead of < (or vice versa) in a loop boundary condition.",
                "Iteration", "AP CS"
        ));
        questions.add(new Question(
                47L,
                "What does the statement i++ do inside a loop header?",
                "Decrements i by 1", "Increments i by 1", "Multiplies i by 2", "Checks if i is positive",
                "Increments i by 1",
                "The post-increment operator i++ adds 1 to the current value of i.",
                "Iteration", "AP CS"
        ));
        questions.add(new Question(
                48L,
                "What is the final value of x after this loop?\nint x = 10;\nwhile (x > 0) {\n    x -= 3;\n}",
                "0", "-1", "-2", "1",
                "-2",
                "Trace: 10 -> 7 -> 4 -> 1 -> -2. At -2, the condition -2 > 0 is false, so the loop terminates with x = -2.",
                "Iteration", "AP CS"
        ));
        questions.add(new Question(
                49L,
                "Which loop structure is best suited when the exact number of iterations is known before looping?",
                "while loop", "for loop", "do-while loop", "recursion",
                "for loop",
                "A standard for loop neatly bundles initialization, condition check, and update, making it ideal for known iteration counts.",
                "Iteration", "AP CS"
        ));
        questions.add(new Question(
                50L,
                "What is printed?\nfor (int i = 5; i > 2; i--) {\n    System.out.print(i + \" \");\n}",
                "5 4 3 2 ", "5 4 3 ", "4 3 2 ", "5 4 ",
                "5 4 3 ",
                "i begins at 5, decrements to 4, then 3. When i reaches 2, 2 > 2 is false and the loop terminates.",
                "Iteration", "AP CS"
        ));


        // ==========================================
        //  TOPIC 6: Methods (IDs 51-60)
        // ==========================================
        questions.add(new Question(
                51L,
                "What keyword indicates that a method does NOT return any value in Java?",
                "null", "empty", "void", "static",
                "void",
                "The void return type tells the Java compiler that the method performs an action without returning any value.",
                "Methods", "AP CS"
        ));
        questions.add(new Question(
                52L,
                "Which term describes the values passed INTO a method call?",
                "Parameters", "Arguments", "Variables", "Signatures",
                "Arguments",
                "Arguments are the actual values passed to a method when it is called (whereas parameters are variables in the method header).",
                "Methods", "AP CS"
        ));
        questions.add(new Question(
                53L,
                "What keyword is used inside a method to send a result back to the caller?",
                "send", "output", "return", "give",
                "return",
                "The return keyword immediately terminates the method and sends the specified expression's value back to the caller.",
                "Methods", "AP CS"
        ));
        questions.add(new Question(
                54L,
                "What allows two methods in the same class to share the same name?",
                "Method overriding", "Method overloading", "Encapsulation", "Polymorphism",
                "Method overloading",
                "Method overloading occurs when methods have the same name but different parameter lists (type, count, or order).",
                "Methods", "AP CS"
        ));
        questions.add(new Question(
                55L,
                "Given public static int square(int n) { return n * n; }, what does square(4) evaluate to?",
                "8", "16", "4", "0",
                "16",
                "The method multiplies n by itself: 4 * 4 = 16.",
                "Methods", "AP CS"
        ));
        questions.add(new Question(
                56L,
                "In Java, how are primitive arguments passed to methods?",
                "Pass-by-reference", "Pass-by-value", "Pass-by-pointer", "Pass-by-name",
                "Pass-by-value",
                "Java is strictly pass-by-value: a copy of the primitive value is passed, so modifications inside the method do not affect the original variable.",
                "Methods", "AP CS"
        ));
        questions.add(new Question(
                57L,
                "What does the static keyword in a method header mean?",
                "The method cannot be changed", "The method belongs to the class itself, not to any specific instance", "The method must return an integer", "The method is private",
                "The method belongs to the class itself, not to any specific instance",
                "Static methods can be invoked directly on the class (e.g., Math.sqrt) without instantiating an object of the class.",
                "Methods", "AP CS"
        ));
        questions.add(new Question(
                58L,
                "What is a method signature composed of in Java?",
                "The method name and parameter parameter list", "The return type and method name", "The access modifier and return type", "The method body",
                "The method name and parameter parameter list",
                "In Java, a method signature consists of the method's name and its parameter types in order.",
                "Methods", "AP CS"
        ));
        questions.add(new Question(
                59L,
                "What happens if a non-void method reaches the end of its body without executing a return statement?",
                "Returns 0 automatically", "Returns null", "Compile-time error", "Runtime exception",
                "Compile-time error",
                "Java requires all possible execution paths of a non-void method to explicitly return a value of the declared type.",
                "Methods", "AP CS"
        ));
        questions.add(new Question(
                60L,
                "Which method header correctly defines a method that takes two doubles and returns a boolean?",
                "public boolean compare(double a, double b)", "public double compare(boolean a, boolean b)", "public void compare(double a, double b)", "public boolean compare(a, b)",
                "public boolean compare(double a, double b)",
                "The return type (boolean) precedes the method name, followed by parameter declarations with types and names: (double a, double b).",
                "Methods", "AP CS"
        ));


        // ==========================================
        //  TOPIC 7: Classes & Objects (IDs 61-70)
        // ==========================================
        questions.add(new Question(
                61L,
                "Which operator is used to instantiate a new object from a class in Java?",
                "create", "new", "make", "instance",
                "new",
                "The new operator allocates memory for a new object and calls its constructor.",
                "Classes & Objects", "AP CS"
        ));
        questions.add(new Question(
                62L,
                "What is the primary role of a constructor in a Java class?",
                "To destroy an object", "To initialize the instance variables of a newly created object", "To print class details", "To convert data types",
                "To initialize the instance variables of a newly created object",
                "A constructor executes automatically when an object is instantiated with new to set up initial state.",
                "Classes & Objects", "AP CS"
        ));
        questions.add(new Question(
                63L,
                "What naming rule must all constructors in Java follow?",
                "They must start with set", "They must have the exact same name as the class and no return type", "They must be named init", "They must return an instance of the class",
                "They must have the exact same name as the class and no return type",
                "A constructor must share the exact name of the enclosing class and does not specify any return type (not even void).",
                "Classes & Objects", "AP CS"
        ));
        questions.add(new Question(
                64L,
                "Why should instance variables typically be declared with the private access modifier?",
                "To improve program performance", "To prevent direct outside modification and enforce encapsulation", "To make variables accessible everywhere", "To allow inheritance",
                "To prevent direct outside modification and enforce encapsulation",
                "Encapsulation protects object state by restricting direct external access and exposing controlled getter and setter methods.",
                "Classes & Objects", "AP CS"
        ));
        questions.add(new Question(
                65L,
                "What does the this keyword refer to inside an instance method or constructor?",
                "The parent class", "The current object instance", "The main method", "A static variable",
                "The current object instance",
                "this is a reference to the current calling object, commonly used to distinguish instance variables from parameter names.",
                "Classes & Objects", "AP CS"
        ));
        questions.add(new Question(
                66L,
                "What is a method that retrieves the value of a private instance variable called?",
                "Mutator / Setter", "Accessor / Getter", "Constructor", "Initializer",
                "Accessor / Getter",
                "An accessor (or getter) method returns the value of a private field without exposing the field itself directly.",
                "Classes & Objects", "AP CS"
        ));
        questions.add(new Question(
                67L,
                "What is a method that modifies the value of a private instance variable called?",
                "Accessor / Getter", "Mutator / Setter", "Constructor", "Destructor",
                "Mutator / Setter",
                "A mutator (or setter) method changes the value of a private instance variable, often incorporating validation logic.",
                "Classes & Objects", "AP CS"
        ));
        questions.add(new Question(
                68L,
                "What default value does Java assign to an uninitialized boolean instance variable?",
                "true", "false", "null", "0",
                "false",
                "Java automatically initializes instance variables of type boolean to false.",
                "Classes & Objects", "AP CS"
        ));
        questions.add(new Question(
                69L,
                "What is the default reference value of an uninitialized object instance variable?",
                "0", "\"\"", "null", "undefined",
                "null",
                "Object reference variables that have not been assigned to refer to an object hold the special value null.",
                "Classes & Objects", "AP CS"
        ));
        questions.add(new Question(
                70L,
                "If no constructor is explicitly written in a class, what does Java provide?",
                "A compile-time error", "A default no-argument constructor", "A constructor with all parameters", "Nothing",
                "A default no-argument constructor",
                "Java automatically provides a default no-argument constructor if and only if no other constructors are declared in the class.",
                "Classes & Objects", "AP CS"
        ));


        // ==========================================
        //  TOPIC 8: Arrays (IDs 71-80)
        // ==========================================
        questions.add(new Question(
                71L,
                "Which statement correctly declares and instantiates an integer array of size 5?",
                "int[] numbers = new int[5];", "int numbers = new int(5);", "int[] numbers = int[5];", "Array numbers = new Array(5);",
                "int[] numbers = new int[5];",
                "The syntax int[] indicates an integer array, and new int[5] allocates space for 5 integer elements.",
                "Arrays", "AP CS"
        ));
        questions.add(new Question(
                72L,
                "What is the index of the first element in any Java array?",
                "1", "0", "-1", "Depends on array size",
                "0",
                "Java arrays are zero-indexed; the first element is accessed at index 0.",
                "Arrays", "AP CS"
        ));
        questions.add(new Question(
                73L,
                "Given int[] data = {10, 20, 30, 40}; what is the value of data.length?",
                "3", "4", "5", "Compilation error",
                "4",
                "The .length field of an array returns the total number of elements it can hold (4 in this case). Note that length is a field, not a method.",
                "Arrays", "AP CS"
        ));
        questions.add(new Question(
                74L,
                "What happens when code attempts to access data[5] in an array of length 5?",
                "Returns 0", "Returns null", "Throws ArrayIndexOutOfBoundsException", "Wraps around to index 0",
                "Throws ArrayIndexOutOfBoundsException",
                "Valid indices for an array of size 5 are 0 through 4. Index 5 is out of bounds and throws ArrayIndexOutOfBoundsException.",
                "Arrays", "AP CS"
        ));
        questions.add(new Question(
                75L,
                "Given int[] arr = {4, 7, 2, 9}; what expression accesses the last element?",
                "arr[arr.length]", "arr[arr.length - 1]", "arr[last]", "arr.last()",
                "arr[arr.length - 1]",
                "Because indexing starts at 0, the final element is always located at index arr.length - 1.",
                "Arrays", "AP CS"
        ));
        questions.add(new Question(
                76L,
                "What default value is stored in elements of a newly created int[] array?",
                "null", "0", "-1", "undefined",
                "0",
                "When a numeric array is created with new int[n], all elements are automatically initialized to 0.",
                "Arrays", "AP CS"
        ));
        questions.add(new Question(
                77L,
                "What is the output of the following?\nint[] nums = {1, 2, 3};\nnums[1] = 9;\nSystem.out.print(nums[1]);",
                "2", "9", "1", "3",
                "9",
                "Assigning nums[1] = 9 replaces the element at index 1 with 9.",
                "Arrays", "AP CS"
        ));
        questions.add(new Question(
                78L,
                "Can the size of a standard Java array be altered after it has been instantiated?",
                "Yes, by calling arr.resize()", "Yes, by adding more elements", "No, array sizes are fixed upon creation", "Yes, if using an array initializer",
                "No, array sizes are fixed upon creation",
                "Standard Java arrays have a fixed size that cannot change once allocated in memory.",
                "Arrays", "AP CS"
        ));
        questions.add(new Question(
                79L,
                "What does an enhanced for loop (for-each) for (int n : nums) traverse?",
                "The array indices only", "Each element in nums from index 0 to length - 1", "The array in reverse", "Only odd elements",
                "Each element in nums from index 0 to length - 1",
                "The enhanced for loop visits each value in the array sequentially from the first element to the last.",
                "Arrays", "AP CS"
        ));
        questions.add(new Question(
                80L,
                "Which loop header correctly traverses all valid indices of int[] arr?",
                "for (int i = 0; i <= arr.length; i++)", "for (int i = 0; i < arr.length; i++)", "for (int i = 1; i <= arr.length; i++)", "for (int i = 1; i < arr.length; i++)",
                "for (int i = 0; i < arr.length; i++)",
                "Valid indices begin at 0 and strictly stay below arr.length (i < arr.length). Using <= causes an ArrayIndexOutOfBoundsException.",
                "Arrays", "AP CS"
        ));

        // ==========================================
        //  ICSE UNIT 1: OOP & Java Fundamentals (IDs 81-86)
        // ==========================================
        questions.add(new Question(
                81L,
                "Which software translates Java source code (.java) into platform-independent Bytecode (.class)?",
                "Java Virtual Machine (JVM)", "Java Compiler (javac)", "Java Interpreter", "Operating System",
                "Java Compiler (javac)",
                "The javac compiler compiles Java source code into bytecode, which can then run on any JVM.",
                "OOP & Java Fundamentals", "ICSE"
        ));
        questions.add(new Question(
                82L,
                "What is the term for binding data members and member methods together into a single unit in Java?",
                "Inheritance", "Encapsulation", "Polymorphism", "Abstraction",
                "Encapsulation",
                "Encapsulation wraps data (fields) and code (methods) together into a single entity, typically a class.",
                "OOP & Java Fundamentals", "ICSE"
        ));
        questions.add(new Question(
                83L,
                "Which OOP principle represents the ability of an entity to take more than one form, such as method overloading?",
                "Abstraction", "Polymorphism", "Inheritance", "Encapsulation",
                "Polymorphism",
                "Polymorphism literally means 'many forms' and is demonstrated in Java through method overloading and overriding.",
                "OOP & Java Fundamentals", "ICSE"
        ));
        questions.add(new Question(
                84L,
                "Which Java keyword is used to establish inheritance between a subclass and a superclass?",
                "implements", "extends", "inherits", "super",
                "extends",
                "The extends keyword is used to derive a subclass from a superclass in Java.",
                "OOP & Java Fundamentals", "ICSE"
        ));
        questions.add(new Question(
                85L,
                "What kind of language translator executes Java Bytecode line-by-line on the host machine?",
                "Interpreter inside JVM", "Assembler", "Preprocessor", "Text Editor",
                "Interpreter inside JVM",
                "The JVM contains an interpreter that reads and executes bytecode line-by-line for the target architecture.",
                "OOP & Java Fundamentals", "ICSE"
        ));
        questions.add(new Question(
                86L,
                "Which term describes showing only the essential features of an object without including background details?",
                "Encapsulation", "Abstraction", "Overloading", "Instantiation",
                "Abstraction",
                "Abstraction focuses on the observable behavior of an object while hiding internal complexity.",
                "OOP & Java Fundamentals", "ICSE"
        ));

        // ==========================================
        //  ICSE UNIT 2: Operators & Expressions (IDs 87-92)
        // ==========================================
        questions.add(new Question(
                87L,
                "If int a = 5; what is the value of result after: int result = a++ + ++a;",
                "10", "11", "12", "13",
                "12",
                "a++ evaluates to 5 (then a becomes 6). Then ++a increments a to 7 and evaluates to 7. 5 + 7 = 12.",
                "Operators & Expressions", "ICSE"
        ));
        questions.add(new Question(
                88L,
                "Which operator is the only ternary operator in Java?",
                "&&", "instanceof", "? :", "==",
                "? :",
                "The conditional operator (? :) takes three operands: condition ? expression1 : expression2.",
                "Operators & Expressions", "ICSE"
        ));
        questions.add(new Question(
                89L,
                "What is the result of casting (int) 7.9 in Java?",
                "8", "7", "7.9", "Compile error",
                "7",
                "Explicit narrowing casting from double to int truncates the fractional portion, leaving 7.",
                "Operators & Expressions", "ICSE"
        ));
        questions.add(new Question(
                90L,
                "What is the value of 17 % 5 in Java?",
                "3", "2", "3.4", "1",
                "2",
                "The modulo operator (%) calculates the remainder after integer division: 17 divided by 5 is 3 with remainder 2.",
                "Operators & Expressions", "ICSE"
        ));
        questions.add(new Question(
                91L,
                "Automatic conversion of a smaller primitive type to a larger type (e.g. int to double) is called:",
                "Explicit Casting", "Implicit Widening Conversion", "Narrowing", "Boxing",
                "Implicit Widening Conversion",
                "Widening conversion is performed automatically by Java because there is no loss of precision or magnitude.",
                "Operators & Expressions", "ICSE"
        ));
        questions.add(new Question(
                92L,
                "If int x = 10; what is the value of x after: x += 5 * 2;",
                "30", "20", "25", "15",
                "20",
                "Multiplication has higher precedence than compound assignment: 5 * 2 is 10, then x += 10 makes x = 20.",
                "Operators & Expressions", "ICSE"
        ));

        // ==========================================
        //  ICSE UNIT 3: Conditional Branching & Switch (IDs 93-98)
        // ==========================================
        questions.add(new Question(
                93L,
                "What happens in a switch-case construct if a matching case does not have a break statement?",
                "Compilation error", "Program terminates immediately", "Execution falls through to subsequent cases", "Default block executes twice",
                "Execution falls through to subsequent cases",
                "Without a break statement, execution continues into the subsequent case blocks (fall-through behavior).",
                "Conditional Branching & Switch", "ICSE"
        ));
        questions.add(new Question(
                94L,
                "Which data type is NOT permitted as a switch expression in standard Java?",
                "int", "char", "double", "String",
                "double",
                "Floating-point types (float and double) cannot be used in a switch expression due to precision considerations.",
                "Conditional Branching & Switch", "ICSE"
        ));
        questions.add(new Question(
                95L,
                "When is the default block executed in a switch statement?",
                "Always at the end of the switch", "Only when no case matches the expression value", "At the start of the switch", "Only if break is missing",
                "Only when no case matches the expression value",
                "The default block acts as a fallback when none of the explicit case values match.",
                "Conditional Branching & Switch", "ICSE"
        ));
        questions.add(new Question(
                96L,
                "Which keyword immediately transfers control out of a switch block?",
                "continue", "return", "break", "goto",
                "break",
                "The break keyword terminates the switch statement and resumes execution after the closing brace.",
                "Conditional Branching & Switch", "ICSE"
        ));
        questions.add(new Question(
                97L,
                "In an if-else ladder, how many conditional blocks can execute at most?",
                "All matching blocks", "Exactly one", "None or one", "Two",
                "None or one",
                "An if-else ladder evaluates conditions sequentially; as soon as one is true, its block executes and the rest are skipped.",
                "Conditional Branching & Switch", "ICSE"
        ));
        questions.add(new Question(
                98L,
                "What is the output of: int k = 2; switch(k) { case 1: k++; case 2: k+=2; case 3: k+=3; }",
                "2", "4", "7", "5",
                "7",
                "Matches case 2: k becomes 4. Since there is no break, falls through to case 3: k becomes 4 + 3 = 7.",
                "Conditional Branching & Switch", "ICSE"
        ));

        // ==========================================
        //  ICSE UNIT 4: Iterative Constructs & Loops (IDs 99-104)
        // ==========================================
        questions.add(new Question(
                99L,
                "Why is a do-while loop known as an exit-controlled loop in ICSE Computer Applications?",
                "It exits as soon as it begins", "The test condition is evaluated after the body executes", "It uses the break statement to exit", "It can never be infinite",
                "The test condition is evaluated after the body executes",
                "In a do-while loop, the loop body executes first, and the termination condition is checked at the exit.",
                "Iterative Constructs & Loops", "ICSE"
        ));
        questions.add(new Question(
                100L,
                "What is the minimum number of times a do-while loop will execute?",
                "0", "1", "2", "Depends on condition",
                "1",
                "Because the condition is checked at the end, a do-while loop is guaranteed to execute at least once.",
                "Iterative Constructs & Loops", "ICSE"
        ));
        questions.add(new Question(
                101L,
                "What is the difference between break and continue inside a loop?",
                "break skips the current iteration; continue exits the loop", "break terminates the loop; continue skips to the next iteration", "Both do the exact same thing", "continue terminates the whole program",
                "break terminates the loop; continue skips to the next iteration",
                "break immediately ends the enclosing loop; continue jumps directly to the loop update/next iteration.",
                "Iterative Constructs & Loops", "ICSE"
        ));
        questions.add(new Question(
                102L,
                "How many times will this loop execute: for (int i = 10; i >= 1; i -= 2)",
                "10 times", "5 times", "4 times", "Infinite",
                "5 times",
                "Values of i are 10, 8, 6, 4, 2 (5 total iterations). When i becomes 0, the condition i >= 1 is false.",
                "Iterative Constructs & Loops", "ICSE"
        ));
        questions.add(new Question(
                103L,
                "Which loop structure is best suited when the exact number of iterations is known in advance?",
                "while loop", "for loop", "do-while loop", "switch statement",
                "for loop",
                "A for loop cleanly encapsulates initialization, test condition, and increment/decrement into a single header.",
                "Iterative Constructs & Loops", "ICSE"
        ));
        questions.add(new Question(
                104L,
                "What causes a while loop while (n > 0) to become an infinite loop?",
                "Declaring n as an int", "Failing to update or decrement n inside the loop body", "Using a positive initial value for n", "Using braces around the body",
                "Failing to update or decrement n inside the loop body",
                "If n is never decremented, n > 0 remains permanently true, causing an infinite loop.",
                "Iterative Constructs & Loops", "ICSE"
        ));

        // ==========================================
        //  ICSE UNIT 5: Classic Number Programs (IDs 105-110)
        // ==========================================
        questions.add(new Question(
                105L,
                "In ICSE number extraction programs, which operation extracts the last digit of an integer n?",
                "n / 10", "n % 10", "n - 10", "Math.sqrt(n)",
                "n % 10",
                "n % 10 returns the remainder when n is divided by 10, which is always the unit (last) digit.",
                "Classic Number Programs", "ICSE"
        ));
        questions.add(new Question(
                106L,
                "What defines an Armstrong (Narcissistic) number in ICSE Java (e.g. 153)?",
                "Sum of digits equals product of digits", "Sum of the cubes of its digits equals the original number", "Number equals its reverse", "Square of number ends in the number",
                "Sum of the cubes of its digits equals the original number",
                "153 is an Armstrong number because 1^3 + 5^3 + 3^3 = 1 + 125 + 27 = 153.",
                "Classic Number Programs", "ICSE"
        ));
        questions.add(new Question(
                107L,
                "What defines a Palindrome number in Java (e.g. 121, 1331)?",
                "A number divisible only by 1 and itself", "A number that remains identical when its digits are reversed", "Sum of proper divisors equals the number", "A number whose square ends in itself",
                "A number that remains identical when its digits are reversed",
                "A palindrome number reads identically backwards and forwards (e.g., 121 reversed is 121).",
                "Classic Number Programs", "ICSE"
        ));
        questions.add(new Question(
                108L,
                "What defines a Perfect number in ICSE Computer Applications (e.g. 6 or 28)?",
                "Sum of all proper divisors (excluding the number itself) equals the number", "Square root of the number is an integer", "Sum of digits is a prime number", "Number is divisible by 10",
                "Sum of all proper divisors (excluding the number itself) equals the number",
                "For 6, proper divisors are 1, 2, 3: 1 + 2 + 3 = 6. For 28: 1 + 2 + 4 + 7 + 14 = 28.",
                "Classic Number Programs", "ICSE"
        ));
        questions.add(new Question(
                109L,
                "What is an Automorphic number in ICSE Java (e.g. 25)?",
                "A number whose sum of digits is even", "A number whose square ends in the digits of the original number", "A number with prime factors only", "A number equal to the sum of factorials of digits",
                "A number whose square ends in the digits of the original number",
                "25^2 = 625, which ends in 25. Similarly 5^2 = 25, 76^2 = 5776.",
                "Classic Number Programs", "ICSE"
        ));
        questions.add(new Question(
                110L,
                "What is a Spy number in ICSE Java (e.g. 1124)?",
                "Sum of digits equals the product of its digits", "Sum of squares of digits equals number", "Number has an even number of factors", "All digits are distinct",
                "Sum of digits equals the product of its digits",
                "For 1124: Sum = 1 + 1 + 2 + 4 = 8. Product = 1 * 1 * 2 * 4 = 8. Since Sum == Product, it is a Spy number.",
                "Classic Number Programs", "ICSE"
        ));

        // ==========================================
        //  ICSE UNIT 6: Character & String Handling (IDs 111-116)
        // ==========================================
        questions.add(new Question(
                111L,
                "Which Character class method checks whether a given character ch is an alphabetic letter?",
                "Character.isAlpha(ch)", "Character.isLetter(ch)", "Character.isChar(ch)", "Character.isLetterOrDigit(ch)",
                "Character.isLetter(ch)",
                "Character.isLetter(char ch) returns true if the specified character is a letter.",
                "Character & String Handling", "ICSE"
        ));
        questions.add(new Question(
                112L,
                "What is returned by str1.compareTo(str2) if str1 is lexicographically identical to str2?",
                "true", "1", "0", "-1",
                "0",
                "compareTo() returns 0 when two strings are identical in content and case.",
                "Character & String Handling", "ICSE"
        ));
        questions.add(new Question(
                113L,
                "Given String s = \"KOLKATA\"; what is returned by s.substring(3, 6)?",
                "\"LKA\"", "\"KAT\"", "\"LKAT\"", "\"OLK\"",
                "\"KAT\"",
                "Indices: K(0) O(1) L(2) K(3) A(4) T(5) A(6). substring(3, 6) takes indices 3, 4, 5, which is \"KAT\".",
                "Character & String Handling", "ICSE"
        ));
        questions.add(new Question(
                114L,
                "What is a Piglatin word in ICSE String programs for a word starting with consonants (e.g. \"CLEAR\")?",
                "\"CLEARAY\"", "\"EARCLAY\"", "\"LEARCAY\"", "\"CLAYEAR\"",
                "\"EARCLAY\"",
                "Piglatin shifts all consonants before the first vowel ('CL') to the end and appends 'AY': \"EAR\" + \"CL\" + \"AY\" = \"EARCLAY\".",
                "Character & String Handling", "ICSE"
        ));
        questions.add(new Question(
                115L,
                "Which method removes leading and trailing whitespace from a String in Java?",
                "clean()", "stripSpaces()", "trim()", "deleteWhitespace()",
                "trim()",
                "The trim() method returns a copy of the string with leading and trailing whitespaces removed.",
                "Character & String Handling", "ICSE"
        ));
        questions.add(new Question(
                116L,
                "Why should strings be compared using .equals() instead of == in Java?",
                "== does not compile for Strings", ".equals() checks content equality while == checks reference memory addresses", "== is slower than .equals()", ".equals() ignores case differences",
                ".equals() checks content equality while == checks reference memory addresses",
                "The == operator tests whether two references point to the exact same object in memory, not whether the characters match.",
                "Character & String Handling", "ICSE"
        ));

        // ==========================================
        //  ICSE UNIT 7: User-Defined Methods & Overloading (IDs 117-122)
        // ==========================================
        questions.add(new Question(
                117L,
                "In ICSE Java, what distinguishes a pure method from an impure method?",
                "A pure method is public; an impure method is private", "A pure method does not modify the state of the object (no side effects)", "A pure method returns void", "An impure method has no parameters",
                "A pure method does not modify the state of the object (no side effects)",
                "Pure methods compute and return a value without altering the calling object's state, whereas impure methods modify instance variables.",
                "User-Defined Methods & Overloading", "ICSE"
        ));
        questions.add(new Question(
                118L,
                "Which combination correctly qualifies methods for method overloading in a Java class?",
                "Same method name with different return types only", "Same method name with different parameter list (count, types, or order)", "Different method names with identical parameters", "Methods declared in different packages only",
                "Same method name with different parameter list (count, types, or order)",
                "Overloading requires the method names to be identical and parameter signatures to differ. Return type alone is insufficient.",
                "User-Defined Methods & Overloading", "ICSE"
        ));
        questions.add(new Question(
                119L,
                "What is the mechanism by which arguments are passed to methods in Java?",
                "Call by reference for primitives", "Call by value for all types", "Call by name", "Call by pointer",
                "Call by value for all types",
                "Java is strictly pass-by-value: a copy of the primitive value or reference handle is passed into the parameter.",
                "User-Defined Methods & Overloading", "ICSE"
        ));
        questions.add(new Question(
                120L,
                "What does the static keyword signify when placed in a method declaration?",
                "The method cannot be called from main", "The method belongs to the class itself and can be called without creating an object", "The method returns an integer", "The method cannot have parameters",
                "The method belongs to the class itself and can be called without creating an object",
                "Static methods are invoked using the class name directly (e.g. Math.pow) without requiring class instantiation.",
                "User-Defined Methods & Overloading", "ICSE"
        ));
        questions.add(new Question(
                121L,
                "What return type is specified for a method that does not return any value to the caller?",
                "null", "empty", "void", "static",
                "void",
                "The void keyword specifies that a method completes its execution without returning any data to the invoking statement.",
                "User-Defined Methods & Overloading", "ICSE"
        ));
        questions.add(new Question(
                122L,
                "What is the signature of a method in Java?",
                "Return type + method name", "Method name + parameter list", "Access specifier + return type", "Class name + package name",
                "Method name + parameter list",
                "In Java, a method signature consists strictly of the method name and the types/order of its parameter list.",
                "User-Defined Methods & Overloading", "ICSE"
        ));

        // ==========================================
        //  ICSE UNIT 8: Arrays, Searching & Sorting (IDs 123-128)
        // ==========================================
        questions.add(new Question(
                123L,
                "What is the fundamental prerequisite before performing a Binary Search on an array?",
                "The array must contain only positive numbers", "The array elements must be sorted in ascending or descending order", "The array must have an even length", "The array must be 2-dimensional",
                "The array elements must be sorted in ascending or descending order",
                "Binary search relies on sorted order to discard half of the search interval in each comparison step.",
                "Arrays, Searching & Sorting", "ICSE"
        ));
        questions.add(new Question(
                124L,
                "In Bubble Sort algorithm for an array of n elements, how many passes does the outer loop require?",
                "n passes", "n - 1 passes", "n / 2 passes", "n * n passes",
                "n - 1 passes",
                "After n - 1 passes, all n elements are guaranteed to be in their correct sorted positions.",
                "Arrays, Searching & Sorting", "ICSE"
        ));
        questions.add(new Question(
                125L,
                "In a square 2D matrix arr[N][N], what condition identifies elements on the principal (left) diagonal?",
                "row + col == N - 1", "row == col", "row > col", "col == 0",
                "row == col",
                "On the left diagonal, row and column indices are equal: [0][0], [1][1], [2][2], etc.",
                "Arrays, Searching & Sorting", "ICSE"
        ));
        questions.add(new Question(
                126L,
                "In a square 2D matrix arr[N][N], what condition identifies elements on the secondary (right) diagonal?",
                "row == col", "row + col == N - 1", "row == 0", "col == N - 1",
                "row + col == N - 1",
                "On the secondary diagonal of an N x N matrix, the sum of row index and column index is always N - 1.",
                "Arrays, Searching & Sorting", "ICSE"
        ));
        questions.add(new Question(
                127L,
                "What is the time complexity advantage of Binary Search over Linear Search for large sorted arrays?",
                "Binary Search is O(log n); Linear Search is O(n)", "Binary Search is O(n); Linear Search is O(1)", "Both have identical O(n) performance", "Linear search is faster",
                "Binary Search is O(log n); Linear Search is O(n)",
                "Binary search repeatedly divides the search space in half (O(log n)), making it vastly faster than linear traversal (O(n)).",
                "Arrays, Searching & Sorting", "ICSE"
        ));
        questions.add(new Question(
                128L,
                "How do you find the total number of rows in a 2D array int[][] matrix in Java?",
                "matrix.length", "matrix[0].length", "matrix.size()", "matrix.rows",
                "matrix.length",
                "matrix.length yields the number of rows, while matrix[0].length gives the number of columns in row 0.",
                "Arrays, Searching & Sorting", "ICSE"
        ));

        // ICSE units 1-8: questions 129-160 (brings every ICSE unit to 10 questions)
        questions.addAll(IcseExtraQuestions.all());

        // ISC Computer Science (Class 11 & 12): questions 201-280, 10 per unit
        questions.addAll(IscQuestions.all());
    }

    public List<Question> findAll() {
        return Collections.unmodifiableList(questions);
    }

    public Optional<Question> findById(Long id) {
        return questions.stream()
                .filter(question -> question.getId().equals(id))
                .findFirst();
    }

    public List<Question> findByTopic(String topic) {

        List<Question> result = questions.stream()
                .filter(question ->
                        question.getTopic().equalsIgnoreCase(topic))
                .toList();

        List<Question> shuffled = new ArrayList<>(result);

        Collections.shuffle(shuffled);

        return shuffled;
    }

    public List<Question> findByCurriculum(String curriculum) {

        return questions.stream()
                .filter(question ->
                        question.getCurriculum().equalsIgnoreCase(curriculum))
                .toList();
    }

    public List<Question> findByTopicAndCurriculum(String topic, String curriculum) {
        List<Question> result = questions.stream()
                .filter(question ->
                        question.getTopic().equalsIgnoreCase(topic) &&
                        question.getCurriculum().equalsIgnoreCase(curriculum))
                .toList();

        List<Question> shuffled = new ArrayList<>(result);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    public List<Question> generateMockExam(int questionsPerTopic) {
        String[] topics = {
                "Variables & Data Types",
                "Expressions & Assignment",
                "Strings",
                "Selection",
                "Iteration",
                "Methods",
                "Classes & Objects",
                "Arrays"
        };

        List<Question> examQuestions = new ArrayList<>();

        for (String topic : topics) {
            List<Question> topicPool = new ArrayList<>(findByTopicAndCurriculum(topic, "AP CS"));
            Collections.shuffle(topicPool);
            int take = Math.min(questionsPerTopic, topicPool.size());
            examQuestions.addAll(topicPool.subList(0, take));
        }

        Collections.shuffle(examQuestions);
        return examQuestions;
    }

    public List<Question> generateIcseMockExam(int questionsPerTopic) {
        String[] icseTopics = {
                "OOP & Java Fundamentals",
                "Operators & Expressions",
                "Conditional Branching & Switch",
                "Iterative Constructs & Loops",
                "Classic Number Programs",
                "Character & String Handling",
                "User-Defined Methods & Overloading",
                "Arrays, Searching & Sorting"
        };

        List<Question> examQuestions = new ArrayList<>();

        for (String topic : icseTopics) {
            List<Question> topicPool = new ArrayList<>(findByTopicAndCurriculum(topic, "ICSE"));
            Collections.shuffle(topicPool);
            int take = Math.min(questionsPerTopic, topicPool.size());
            examQuestions.addAll(topicPool.subList(0, take));
        }

        Collections.shuffle(examQuestions);
        return examQuestions;
    }

    /** ISC mock exam: {@code questionsPerTopic} random questions from each of the 8 ISC units, shuffled. */
    public List<Question> generateIscMockExam(int questionsPerTopic) {
        List<Question> examQuestions = new ArrayList<>();
        for (String topic : IscQuestions.TOPICS) {
            List<Question> topicPool = new ArrayList<>(findByTopicAndCurriculum(topic, IscQuestions.ISC));
            Collections.shuffle(topicPool);
            examQuestions.addAll(topicPool.subList(0, Math.min(questionsPerTopic, topicPool.size())));
        }
        Collections.shuffle(examQuestions);
        return examQuestions;
    }
}
