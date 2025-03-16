# Kotlin Analyzer

This is a project for applying to JetBrains Internship 2025 3.0. The task is to write a kotlin program, which will print all public declarations from a Kotlin program/library sources.

## My journey through this project

In the beginning, I would like to emphasize that I have almost no experience working with Kotlin.
I have been working with Java for a long regarding mobile applications and now, during my 6th semester on Uni, I am trying to switch from Java to Kotlin while developing mobile apps.

It seems very easy to switch from Java to Kotlin, so we will see where it will go :)

> Some time later...

So, I learned about PSI or Program Structure Interface, which is a part of the IntelliJ Platform. I was worried that I should write a parser for Kotlin files myself, but it seems that I 
will be able to use PSI to get the Abstract Syntax Tree of the Kotlin code.

> Even more time later...

After complex research and trial and error, I managed to get the PSI tree of the Kotlin file. I have written a simple program that reads the Kotlin file and prints the public declarations.

I've also written some tests to check if the program works correctly.

It was tested with the [Exposed](https://github.com/JetBrains/Exposed) library as it was suggested in the task description.
It seems that the program works correctly and prints all public declarations from the Exposed library.

## How does the program work?

- At the beginning, the program checks if the user has provided the path to the Kotlin file or directory.
If the user has not provided the path, the program will use the default path to the `testDir` directory to showcase the program.

- Next, the program reads the file / files in directory and subdirectories and parses them using the LightVirtualFile 
and creates a Project Structure Interface (PSI) tree.

- Finally, for each found file, the program searches for public declarations and adds them to the string builder, while also formatting the output
and printing it to the console.