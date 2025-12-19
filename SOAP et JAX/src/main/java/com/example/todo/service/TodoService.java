package com.example.todo.service;

import com.example.todo.model.Todo;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class TodoService {

    private static final String XML_FILE_PATH = "src/main/resources/todos.xml";

    @XmlRootElement(name = "todos")
    public static class TodoListWrapper {
        private List<Todo> todos;

        public TodoListWrapper() {
            this.todos = new ArrayList<>();
        }

        public TodoListWrapper(List<Todo> todos) {
            this.todos = todos;
        }

        @XmlElement(name = "todo")
        public List<Todo> getTodos() {
            return todos;
        }

        public void setTodos(List<Todo> todos) {
            this.todos = todos;
        }
    }

    private List<Todo> loadTodos() {
        try {
            File file = new File(XML_FILE_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }

            JAXBContext context = JAXBContext.newInstance(TodoListWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            TodoListWrapper wrapper = (TodoListWrapper) unmarshaller.unmarshal(file);
            return wrapper.getTodos() != null ? wrapper.getTodos() : new ArrayList<>();
        } catch (JAXBException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveTodos(List<Todo> todos) {
        try {
            JAXBContext context = JAXBContext.newInstance(TodoListWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            TodoListWrapper wrapper = new TodoListWrapper(todos);
            File file = new File(XML_FILE_PATH);
            file.getParentFile().mkdirs();
            marshaller.marshal(wrapper, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public List<Todo> findAll() {
        return loadTodos();
    }

    public Todo findById(int id) {
        return loadTodos().stream()
                .filter(todo -> todo.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public String add(Todo todo) {
        List<Todo> todos = loadTodos();
        int maxId = todos.stream()
                .mapToInt(Todo::getId)
                .max()
                .orElse(0);
        todo.setId(maxId + 1);
        todos.add(todo);
        saveTodos(todos);
        return "Todo ajouté avec succès";
    }

    public String update(Todo todo) {
        List<Todo> todos = loadTodos();
        boolean found = false;
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId() == todo.getId()) {
                todos.set(i, todo);
                found = true;
                break;
            }
        }
        if (!found) {
            return "Todo non trouvé";
        }
        saveTodos(todos);
        return "Todo mis à jour avec succès";
    }

    public String delete(int id) {
        List<Todo> todos = loadTodos();
        boolean removed = todos.removeIf(todo -> todo.getId() == id);
        if (!removed) {
            return "Non trouvé";
        }
        saveTodos(todos);
        return "Todo supprimé avec succès";
    }
}
