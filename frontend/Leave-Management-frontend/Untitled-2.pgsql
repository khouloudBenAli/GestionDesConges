SELECT * FROM departments;

SELECT id, first_name, last_name, email, job_title, department_id
FROM employees;

SELECT e.first_name, e.last_name, d.name AS department
FROM employees e
LEFT JOIN departments d ON d.id = e.department_id;