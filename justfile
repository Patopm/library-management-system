set shell := ["zsh", "-cu"]

default:
  @just --list

# Run the full build and tests.
build:
  mvn clean install

# Run all tests.
test:
  mvn test

# Compile all source code.
compile:
  mvn compile

# Run the terminal UI app.
run:
  mvn exec:java -Dexec.mainClass="com.library.Main"

# Package without running tests.
package:
  mvn -DskipTests package

# Remove build artifacts.
clean:
  mvn clean
