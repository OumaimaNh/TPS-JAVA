const SOAP_URL = 'http://localhost:8080/ws/todo';

window.onload = function() {
    loadTodos();
};

function loadTodos() {
    const soapEnvelope = `<?xml version="1.0" encoding="UTF-8"?>
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                          xmlns:tns="http://endpoint.todo.example.com/">
            <soapenv:Header/>
            <soapenv:Body>
                <tns:getAll/>
            </soapenv:Body>
        </soapenv:Envelope>`;

    fetch(SOAP_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'text/xml;charset=UTF-8'
        },
        body: soapEnvelope
    })
    .then(response => response.text())
    .then(xml => {
        const todos = parseTodosFromXML(xml);
        displayTodos(todos);
    })
    .catch(error => {
        console.error('Erreur lors du chargement des todos:', error);
        alert('Erreur: Impossible de charger les todos. Vérifiez que le serveur est démarré.');
    });
}

function parseTodosFromXML(xml) {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xml, 'text/xml');
    const todoElements = xmlDoc.querySelectorAll('return');
    
    const todos = [];
    todoElements.forEach(element => {
        const id = element.querySelector('id')?.textContent;
        const title = element.querySelector('title')?.textContent;
        const completed = element.querySelector('completed')?.textContent === 'true';
        
        if (id && title) {
            todos.push({ id, title, completed });
        }
    });
    
    return todos;
}

function displayTodos(todos) {
    const tbody = document.getElementById('todoTableBody');
    tbody.innerHTML = '';
    
    if (todos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">Aucune tâche</td></tr>';
        return;
    }
    
    todos.forEach(todo => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${todo.id}</td>
            <td>${todo.title}</td>
            <td>${todo.completed ? '✅' : '❌'}</td>
            <td>
                <button class="btn btn-warning btn-sm" onclick="openEditModal(${todo.id}, '${todo.title.replace(/'/g, "\\'")}', ${todo.completed})">Mod</button>
                <button class="btn btn-danger btn-sm" onclick="deleteTodo(${todo.id})">Sup</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function openAddModal() {
    document.getElementById('modalTitle').textContent = 'Ajouter un Todo';
    document.getElementById('todoId').value = '';
    document.getElementById('todoTitle').value = '';
    document.getElementById('todoCompleted').checked = false;
    
    const modal = new bootstrap.Modal(document.getElementById('todoModal'));
    modal.show();
}

function openEditModal(id, title, completed) {
    document.getElementById('modalTitle').textContent = 'Modifier Todo';
    document.getElementById('todoId').value = id;
    document.getElementById('todoTitle').value = title;
    document.getElementById('todoCompleted').checked = completed;
    
    const modal = new bootstrap.Modal(document.getElementById('todoModal'));
    modal.show();
}

function saveTodo() {
    const id = document.getElementById('todoId').value;
    const title = document.getElementById('todoTitle').value.trim();
    const completed = document.getElementById('todoCompleted').checked;
    
    if (!title) {
        alert('Le titre ne peut pas être vide');
        return;
    }
    
    let soapEnvelope;
    
    if (id) {
        soapEnvelope = `<?xml version="1.0" encoding="UTF-8"?>
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                              xmlns:tns="http://endpoint.todo.example.com/">
                <soapenv:Header/>
                <soapenv:Body>
                    <tns:updateTodo>
                        <id>${id}</id>
                        <title>${title}</title>
                        <isCompleted>${completed}</isCompleted>
                    </tns:updateTodo>
                </soapenv:Body>
            </soapenv:Envelope>`;
    } else {
        soapEnvelope = `<?xml version="1.0" encoding="UTF-8"?>
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                              xmlns:tns="http://endpoint.todo.example.com/">
                <soapenv:Header/>
                <soapenv:Body>
                    <tns:addTodo>
                        <title>${title}</title>
                        <isCompleted>${completed}</isCompleted>
                    </tns:addTodo>
                </soapenv:Body>
            </soapenv:Envelope>`;
    }
    
    fetch(SOAP_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'text/xml;charset=UTF-8'
        },
        body: soapEnvelope
    })
    .then(response => response.text())
    .then(() => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('todoModal'));
        modal.hide();
        loadTodos();
    })
    .catch(error => {
        console.error('Erreur lors de la sauvegarde:', error);
        alert('Erreur lors de la sauvegarde du todo');
    });
}

function deleteTodo(id) {
    if (!confirm('Supprimer ce todo ?')) {
        return;
    }
    
    const soapEnvelope = `<?xml version="1.0" encoding="UTF-8"?>
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                          xmlns:tns="http://endpoint.todo.example.com/">
            <soapenv:Header/>
            <soapenv:Body>
                <tns:deleteTodo>
                    <id>${id}</id>
                </tns:deleteTodo>
            </soapenv:Body>
        </soapenv:Envelope>`;
    
    fetch(SOAP_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'text/xml;charset=UTF-8'
        },
        body: soapEnvelope
    })
    .then(response => response.text())
    .then(() => {
        loadTodos();
    })
    .catch(error => {
        console.error('Erreur lors de la suppression:', error);
        alert('Erreur lors de la suppression du todo');
    });
}
