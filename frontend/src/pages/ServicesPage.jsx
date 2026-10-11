import { useEffect, useState } from "react";
import api from "../api/api";

function ServicesPage() {
    const [services, setServices] = useState([]);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    const [name, setName] = useState("");
    const [price, setPrice] = useState("");
    const [description, setDescription] = useState("");

    const [editingId, setEditingId] = useState(null);

    const loadServices = async () => {
        try {
            const response = await api.get("/services");
            setServices(response.data);
        } catch (error) {
            console.error(error);
            setError("Не удалось загрузить услуги");
        }
    };

    useEffect(() => {
        loadServices();
    }, []);

    const resetForm = () => {
        setName("");
        setPrice("");
        setDescription("");
        setEditingId(null);
    };

    const saveService = async (event) => {
        event.preventDefault();
        setError("");
        setMessage("");

        try {
            const data = {
                name,
                price: Number(price),
                description
            };

            if (editingId) {
                await api.put(`/services/${editingId}`, data);
                setMessage("Услуга обновлена");
            } else {
                await api.post("/services", data);
                setMessage("Услуга добавлена");
            }

            resetForm();
            await loadServices();
        } catch (error) {
            console.error(error);
            setError(
                error.response?.data?.message ||
                "Не удалось сохранить услугу"
            );
        }
    };

    const editService = (service) => {
        setEditingId(service.id);
        setName(service.name);
        setPrice(service.price);
        setDescription(service.description || "");
        setMessage("");
        setError("");
    };

    const deleteService = async (id) => {
        if (!window.confirm("Удалить эту услугу?")) {
            return;
        }

        try {
            await api.delete(`/services/${id}`);
            setMessage("Услуга удалена");
            await loadServices();
        } catch (error) {
            console.error(error);
            setError(
                error.response?.data?.message ||
                "Не удалось удалить услугу"
            );
        }
    };

    return (
        <div>
            <h1>Услуги</h1>

            <form onSubmit={saveService}>
                <h2>{editingId ? "Редактирование услуги" : "Новая услуга"}</h2>

                <div>
                    <label>Название</label><br />
                    <input
                        value={name}
                        onChange={(event) => setName(event.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Цена</label><br />
                    <input
                        type="number"
                        step="0.01"
                        min="0"
                        value={price}
                        onChange={(event) => setPrice(event.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Описание</label><br />
                    <textarea
                        value={description}
                        onChange={(event) => setDescription(event.target.value)}
                    />
                </div>

                <button type="submit">
                    {editingId ? "Сохранить изменения" : "Добавить услугу"}
                </button>

                {editingId && (
                    <button type="button" onClick={resetForm}>
                        Отмена
                    </button>
                )}
            </form>

            {message && <p>{message}</p>}
            {error && <p>{error}</p>}

            <h2>Список услуг</h2>

            <table border="1">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Название</th>
                        <th>Цена</th>
                        <th>Описание</th>
                        <th>Действия</th>
                    </tr>
                </thead>

                <tbody>
                    {services.map((service) => (
                        <tr key={service.id}>
                            <td>{service.id}</td>
                            <td>{service.name}</td>
                            <td>{service.price}</td>
                            <td>{service.description}</td>
                            <td>
                                <button onClick={() => editService(service)}>
                                    Изменить
                                </button>

                                <button onClick={() => deleteService(service.id)}>
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

export default ServicesPage;