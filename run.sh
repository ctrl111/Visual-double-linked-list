#!/bin/bash

echo "===================================="
echo "DataStructLab Kotlin Version"
echo "===================================="
echo ""
echo "Compiling and running the application..."
echo ""

mvn clean compile
if [ $? -ne 0 ]; then
    echo ""
    echo "Compilation failed! Please check the errors above."
    exit 1
fi

echo ""
echo "Starting JavaFX application..."
echo ""

mvn javafx:run

