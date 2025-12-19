package com.example.todo.endpoint;

import com.example.todo.model.Todo;
import com.example.todo.service.TodoService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@WebService(serviceName = "TodoService")
@Component
public class TodoEndpoint {

    @Autowired
    private TodoService todoService;

    @WebMethod
    public List<Todo> getAll() {
        return todoService.findAll();
    }

    @WebMethod
    public Todo getById(@WebParam(name = "id") int id) {
        return todoService.findById(id);
    }

    @WebMethod
    public String addTodo(
            @WebParam(name = "title") String title,
            @WebParam(name = "isCompleted") boolean isCompleted) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setCompleted(isCompleted);
        return todoService.add(todo);
    }

    @WebMethod
    public String updateTodo(
            @WebParam(name = "id") int id,
            @WebParam(name = "title") String title,
            @WebParam(name = "isCompleted") boolean isCompleted) {
        Todo todo = new Todo(id, title, isCompleted);
        return todoService.update(todo);
    }

    @WebMethod
    public String deleteTodo(@WebParam(name = "id") int id) {
        return todoService.delete(id);
    }
}
