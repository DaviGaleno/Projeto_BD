const resources = {
    alunos: {
        label: "Alunos",
        endpoint: "/alunos",
        fields: [
            { name: "nomeCompleto", label: "Nome completo", type: "text", required: true },
            { name: "email", label: "E-mail", type: "email", required: true },
            { name: "cpf", label: "CPF", type: "text", required: true }
        ],
        columns: [
            { key: "id", label: "ID" },
            { key: "nomeCompleto", label: "Nome completo" },
            { key: "email", label: "E-mail" },
            { key: "cpf", label: "CPF" }
        ],
        buildPayload: values => values
    },
    professores: {
        label: "Professores",
        endpoint: "/professores",
        fields: [
            { name: "nomeCompleto", label: "Nome completo", type: "text", required: true },
            { name: "email", label: "E-mail", type: "email", required: true },
            { name: "cpf", label: "CPF", type: "text", required: true }
        ],
        columns: [
            { key: "id", label: "ID" },
            { key: "nomeCompleto", label: "Nome completo" },
            { key: "email", label: "E-mail" },
            { key: "cpf", label: "CPF" }
        ],
        buildPayload: values => values
    },
    disciplinas: {
        label: "Disciplinas",
        endpoint: "/disciplinas",
        fields: [
            { name: "nome", label: "Disciplina", type: "text", required: true },
            { name: "cargaHoraria", label: "Carga horaria", type: "number", required: true },
            { name: "professorId", label: "ID do professor", type: "number", required: true }
        ],
        columns: [
            { key: "id", label: "ID" },
            { key: "nome", label: "Disciplina" },
            { key: "cargaHoraria", label: "Carga horaria" },
            { key: "professor.nomeCompleto", label: "Professor" }
        ],
        buildPayload: values => ({
            nome: values.nome,
            cargaHoraria: Number(values.cargaHoraria),
            professor: { id: Number(values.professorId) }
        }),
        fillForm: record => ({
            nome: record.nome,
            cargaHoraria: record.cargaHoraria,
            professorId: record.professor?.id
        })
    },
    matriculas: {
        label: "Matriculas",
        endpoint: "/matriculas",
        fields: [
            { name: "alunoId", label: "ID do aluno", type: "number", required: true },
            { name: "disciplinaId", label: "ID da disciplina", type: "number", required: true },
            { name: "nota1", label: "Nota 1", type: "number", step: "0.1" },
            { name: "nota2", label: "Nota 2", type: "number", step: "0.1" },
            {
                name: "status",
                label: "Status",
                type: "select",
                options: ["MATRICULADO", "APROVADO", "REPROVADO", "TRANCADO", "DESLIGADO"]
            }
        ],
        columns: [
            { key: "id", label: "ID" },
            { key: "aluno.nomeCompleto", label: "Aluno" },
            { key: "disciplina.nome", label: "Disciplina" },
            { key: "nota1", label: "Nota 1" },
            { key: "nota2", label: "Nota 2" },
            { key: "status", label: "Status" }
        ],
        buildPayload: values => ({
            aluno: { id: Number(values.alunoId) },
            disciplina: { id: Number(values.disciplinaId) },
            nota1: values.nota1 === "" ? null : Number(values.nota1),
            nota2: values.nota2 === "" ? null : Number(values.nota2),
            status: values.status || null
        }),
        fillForm: record => ({
            alunoId: record.aluno?.id,
            disciplinaId: record.disciplina?.id,
            nota1: record.nota1,
            nota2: record.nota2,
            status: record.status
        })
    }
};

let activeResource = "alunos";
let reportMetadata = [];

const crudResource = document.querySelector("#crud-resource");
const crudForm = document.querySelector("#crud-form");
const formFields = document.querySelector("#form-fields");
const recordId = document.querySelector("#record-id");
const formTitle = document.querySelector("#form-title");
const crudHead = document.querySelector("#crud-head");
const crudBody = document.querySelector("#crud-body");
const reportTable = document.querySelector("#report-table");
const reportFields = document.querySelector("#report-fields");
const reportForm = document.querySelector("#report-form");
const reportHead = document.querySelector("#report-head");
const reportBody = document.querySelector("#report-body");
const reportCount = document.querySelector("#report-count");
const toast = document.querySelector("#toast");

document.querySelectorAll(".tab").forEach(tab => {
    tab.addEventListener("click", () => {
        document.querySelectorAll(".tab, .view").forEach(item => item.classList.remove("active"));
        tab.classList.add("active");
        document.querySelector(`#${tab.dataset.view}`).classList.add("active");
    });
});

document.querySelector("#new-record").addEventListener("click", resetForm);
document.querySelector("#cancel-edit").addEventListener("click", resetForm);
document.querySelector("#reload-records").addEventListener("click", loadRecords);
document.querySelector("#export-csv").addEventListener("click", exportCsv);

crudResource.addEventListener("change", () => {
    activeResource = crudResource.value;
    renderForm();
    resetForm();
    loadRecords();
});

crudForm.addEventListener("submit", async event => {
    event.preventDefault();
    const config = resources[activeResource];
    const values = readFormValues(config.fields);
    const id = recordId.value;
    const method = id ? "PUT" : "POST";
    const url = id ? `${config.endpoint}/${id}` : config.endpoint;

    await api(url, {
        method,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(config.buildPayload(values))
    });

    notify(id ? "Registro atualizado." : "Registro criado.");
    resetForm();
    loadRecords();
});

reportTable.addEventListener("change", renderReportFields);
reportForm.addEventListener("submit", async event => {
    event.preventDefault();
    await generateReport();
});

init();

async function init() {
    Object.entries(resources).forEach(([key, value]) => {
        crudResource.add(new Option(value.label, key));
    });

    renderForm();
    await Promise.all([loadRecords(), loadReportMetadata()]);
}

function renderForm() {
    const config = resources[activeResource];
    formFields.innerHTML = "";
    config.fields.forEach(field => {
        const label = document.createElement("label");
        label.textContent = field.label;

        let input;
        if (field.type === "select") {
            input = document.createElement("select");
            input.add(new Option("", ""));
            field.options.forEach(option => input.add(new Option(option, option)));
        } else {
            input = document.createElement("input");
            input.type = field.type;
            if (field.step) input.step = field.step;
        }

        input.name = field.name;
        input.required = Boolean(field.required);
        label.append(input);
        formFields.append(label);
    });
}

async function loadRecords() {
    const config = resources[activeResource];
    const data = await api(config.endpoint);
    renderCrudTable(config, data);
}

function renderCrudTable(config, records) {
    crudHead.innerHTML = `<tr>${config.columns.map(col => `<th>${col.label}</th>`).join("")}<th>Acoes</th></tr>`;
    crudBody.innerHTML = "";

    records.forEach(record => {
        const tr = document.createElement("tr");
        config.columns.forEach(col => {
            const td = document.createElement("td");
            td.textContent = valueByPath(record, col.key) ?? "";
            tr.append(td);
        });

        const actions = document.createElement("td");
        actions.className = "actions-cell";
        actions.innerHTML = `
            <div class="row-actions">
                <button type="button" class="secondary" data-action="edit">Editar</button>
                <button type="button" class="danger" data-action="delete">Excluir</button>
            </div>`;
        actions.querySelector('[data-action="edit"]').addEventListener("click", () => editRecord(record));
        actions.querySelector('[data-action="delete"]').addEventListener("click", () => deleteRecord(record.id));
        tr.append(actions);
        crudBody.append(tr);
    });
}

function editRecord(record) {
    const config = resources[activeResource];
    const values = config.fillForm ? config.fillForm(record) : record;
    recordId.value = record.id;
    formTitle.textContent = `Editar ${config.label}`;
    config.fields.forEach(field => {
        const input = crudForm.elements[field.name];
        input.value = values[field.name] ?? "";
    });
}

async function deleteRecord(id) {
    const config = resources[activeResource];
    if (!confirm("Deseja excluir este registro?")) return;
    await api(`${config.endpoint}/${id}`, { method: "DELETE" });
    notify("Registro excluido.");
    resetForm();
    loadRecords();
}

function resetForm() {
    crudForm.reset();
    recordId.value = "";
    formTitle.textContent = `Novo ${resources[activeResource].label}`;
}

function readFormValues(fields) {
    return fields.reduce((values, field) => {
        values[field.name] = crudForm.elements[field.name].value;
        return values;
    }, {});
}

async function loadReportMetadata() {
    reportMetadata = await api("/relatorios/metadados");
    reportMetadata.forEach(table => reportTable.add(new Option(table.rotulo, table.nome)));
    renderReportFields();
}

function renderReportFields() {
    const table = reportMetadata.find(item => item.nome === reportTable.value);
    reportFields.innerHTML = "";
    if (!table) return;

    table.campos.forEach((field, index) => {
        const label = document.createElement("label");
        label.className = "check";
        label.innerHTML = `<input type="checkbox" value="${field.nome}" ${index < 4 ? "checked" : ""}> ${field.rotulo}`;
        reportFields.append(label);
    });
}

async function generateReport() {
    const response = await api("/relatorios/gerar", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(reportPayload())
    });
    renderReport(response);
}

function renderReport(report) {
    reportHead.innerHTML = `<tr>${report.cabecalhos.map(header => `<th>${header}</th>`).join("")}</tr>`;
    reportBody.innerHTML = "";
    report.linhas.forEach(row => {
        const tr = document.createElement("tr");
        Object.values(row).forEach(value => {
            const td = document.createElement("td");
            td.textContent = value ?? "";
            tr.append(td);
        });
        reportBody.append(tr);
    });
    reportCount.textContent = `${report.linhas.length} linhas`;
}

async function exportCsv() {
    const response = await fetch("/relatorios/exportar-csv", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(reportPayload())
    });

    if (!response.ok) {
        throw new Error(await response.text());
    }

    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "relatorio-personalizado.csv";
    link.click();
    URL.revokeObjectURL(url);
}

function reportPayload() {
    return {
        tabela: reportTable.value,
        termo: document.querySelector("#report-search").value,
        campos: Array.from(reportFields.querySelectorAll("input:checked")).map(input => input.value)
    };
}

async function api(url, options = {}) {
    const response = await fetch(url, options);
    if (!response.ok) {
        const message = await response.text();
        notify("Erro na requisicao.");
        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }
    return response.json();
}

function valueByPath(object, path) {
    return path.split(".").reduce((current, key) => current?.[key], object);
}

function notify(message) {
    toast.textContent = message;
    toast.classList.add("show");
    setTimeout(() => toast.classList.remove("show"), 2600);
}
