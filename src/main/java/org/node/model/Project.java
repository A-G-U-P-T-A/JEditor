package org.node.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Project {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("rootPath")
    private String rootPath;
    
    @JsonProperty("nodes")
    private List<String> nodes;
    
    @JsonProperty("connections")
    private List<String> connections;
    
    private static final String PROJECT_FILE = "project.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public Project() {
        // Default constructor for Jackson
        this.nodes = new ArrayList<>();
        this.connections = new ArrayList<>();
    }
    
    public Project(String name, String rootPath) {
        this();
        this.name = name;
        this.rootPath = rootPath;
    }
    
    public static Project createNew(String projectPath) throws IOException {
        Path path = Paths.get(projectPath);
        String projectName = path.getFileName().toString();
        
        // Create project directory if it doesn't exist
        Files.createDirectories(path);
        
        // Create new project instance
        Project project = new Project(projectName, projectPath);
        
        // Save project file
        project.save();
        
        return project;
    }
    
    public static Project load(String projectPath) throws IOException {
        Path projectFile = Paths.get(projectPath, PROJECT_FILE);
        if (!Files.exists(projectFile)) {
            throw new FileNotFoundException("Project file not found: " + projectFile);
        }
        
        return mapper.readValue(projectFile.toFile(), Project.class);
    }
    
    public void save() throws IOException {
        Path projectFile = Paths.get(rootPath, PROJECT_FILE);
        mapper.writerWithDefaultPrettyPrinter().writeValue(projectFile.toFile(), this);
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRootPath() { return rootPath; }
    public void setRootPath(String rootPath) { this.rootPath = rootPath; }
    public List<String> getNodes() { return nodes; }
    public List<String> getConnections() { return connections; }
}
