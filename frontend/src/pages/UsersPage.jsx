import { useEffect, useState } from "react";
import api from "../api/api";

function UsersPage() {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("MASTER");

    const [editingId, setEditingId] = useState(null);

    const loadUsers = async () => {
        try {
            const response = await api.get("/users");
            setUsers(response.data);
        } catch (error) {
            console.error(error);
            setError(
                error.response?.data?.message ||
                "Не удалось загрузить пользователей"
            );
        }
    };

    useEffect(() => {
        loadUsers();
    }, []);

    const resetForm = () => {
        setName("");
        setEmail("");
        setPassword("");
        setRole("MASTER");
        setEditingId(null);
    };

    const saveUser = async (event) => {
        event.preventDefault();

        setError("");
        setMessage("");

        try {
            if (editingId) {
                await api.put(`/users/${editingId}`, {
                    name,
                    email
                });

                setMessage("Пользователь обновлён");
            } else {
                await api.post("/users", {
                    name,
                    email,
                    password,
                    role
                });

                setMessage("Пользователь добавлен");
            }

            resetForm();
            await loadUsers();
        } catch (error) {
            console.error(error);

            setError(
                error.response?.data?.message ||
                "Не удалось сохранить пользователя"
            );
        }
    };

    const editUser = (user) => {
        setEditingId(user.id);
        setName(user.name);
        setEmail(user.email);
        setPassword("");
        setRole(user.role);

        setError("");
        setMessage("");
    };

    const deleteUser = async (id) => {
        if (!window.confirm("Удалить этого пользователя?")) {
            return;
        }

        setError("");
        setMessage("");

        try {
            await api.delete(`/users/${id}`);

            setMessage("Пользователь удалён");
            await loadUsers();
        } catch (error) {
            console.error(error);

            setError(
                error.response?.data?.message ||
                "Не удалось удалить пользователя"
            );
        }
    };

    return (
        <div>
            <h1>Пользователи</h1>

            <form onSubmit={saveUser}>
                <h2>
                    {editingId
                        ? "Редактирование пользователя"
                        : "Новый пользователь"}
                </h2>

                <div>
                    <label>Имя</label><br />
                    <input
                        value={name}
                        onChange={(event) => setName(event.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Email</label><br />
                    <input
                        type="email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        required
                    />
                </div>

                {!editingId && (
                    <div>
                        <label>Пароль</label><br />
                        <input
                            type="password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            required
                            minLength={6}
                        />
                    </div>
                )}


                {!editingId && (
                    <div>
                        <label>Роль</label><br />
                        <select
                            value={role}
                            onChange={(event) => setRole(event.target.value)}
                        >
                            <option value="MASTER">Мастер</option>
                            <option value="MANAGER">Менеджер</option>
                            <option value="ADMIN">Администратор</option>
                        </select>
                    </div>
                )}

                <button type="submit">
                    {editingId
                        ? "Сохранить изменения"
                        : "Добавить пользователя"}
                </button>

                {editingId && (
                    <button type="button" onClick={resetForm}>
                        Отмена
                    </button>
                )}
            </form>

            {message && <p>{message}</p>}
            {error && <p>{error}</p>}

            <h2>Список пользователей</h2>

            <table border="1">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Имя</th>
                        <th>Email</th>
                        <th>Роль</th>
                        <th>Действия</th>
                    </tr>
                </thead>

                <tbody>
                    {users.map((user) => (
                        <tr key={user.id}>
                            <td>{user.id}</td>
                            <td>{user.name}</td>
                            <td>{user.email}</td>
                            <td>{user.role}</td>
                            <td>
                                <button onClick={() => editUser(user)}>
                                    Изменить
                                </button>

                                <button onClick={() => deleteUser(user.id)}>
                                    Удалить
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default UsersPage;