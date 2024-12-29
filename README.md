# Blueprint Node Editor for Java

A powerful visual scripting editor that allows you to create and manipulate Java code through an intuitive node-based interface. Inspired by Unreal Engine's Blueprint system, this editor makes Java programming more visual and accessible.

![Blueprint Node Editor](screenshots/editor.png) *(Add a screenshot of your editor here)*

## Features

- 🎨 **Visual Node-Based Interface**: Create Java programs by connecting nodes instead of writing code
- 🧰 **Java Util Classes Support**: Direct access to Java's utility classes like ArrayList, HashMap, etc.
- 🔌 **Dynamic Node Creation**: Drag and drop to create nodes for constructors and methods
- 🔗 **Visual Connection System**: Connect nodes with wires to establish data flow
- 🎯 **Real-time Preview**: See your node connections and data flow in real-time
- 🌓 **Dark Theme**: Easy on the eyes with a modern dark theme
- 📁 **Project Management**: Save and load your visual scripts (.nedp files)

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- Maven
- JavaFX (included in dependencies)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/NodeEditor.git
cd NodeEditor
```

2. Build the project using Maven:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn javafx:run
```

## Usage

1. **Creating a New Project**:
   - Launch the application
   - Click File -> New Project
   - Start adding nodes to your canvas

2. **Adding Nodes**:
   - Browse the left panel for available Java utility classes
   - Drag methods or constructors onto the canvas
   - Nodes will appear with input and output pins

3. **Making Connections**:
   - Click and drag from an output pin to an input pin
   - Compatible pins will highlight when hovering
   - Right-click a connection to delete it

4. **Managing Projects**:
   - Save your work using File -> Save Project
   - Load existing projects using File -> Open Project
   - Projects are saved in .nedp format

5. **Navigation**:
   - Middle mouse button to pan around the canvas
   - Use the Node Explorer (right panel) to find and focus nodes
   - Select multiple nodes using Ctrl + Click

## Project Structure

```
NodeEditor/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/node/
│   │   │       ├── model/      # Data models and logic
│   │   │       ├── view/       # JavaFX UI components
│   │   │       └── Main.java   # Application entry point
│   │   └── resources/
│   │       └── styles.css      # Application styling
│   └── test/                   # Unit tests
└── pom.xml                     # Maven configuration
```

## Contributing

Feel free to contribute to this project:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Inspired by Unreal Engine's Blueprint system
- Built with JavaFX
- Special thanks to the Java community

## Contact

Your Name - [@yourusername](https://twitter.com/yourusername)

Project Link: [https://github.com/yourusername/NodeEditor](https://github.com/yourusername/NodeEditor)

---
**Note**: This is an ongoing project. Features and documentation will be updated regularly.
