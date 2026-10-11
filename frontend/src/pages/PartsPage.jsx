import { useEffect, useState } from "react";
import api from "../api/api";

function PartsPage() {
    const [parts, setParts] = useState([]);
    const [error, setError] = useState("");
    const [message, setMessage] = useState("");

    const [name, setName] = useState("");
    const [article, setArticle] = useState("");
    const [price, setPrice] = useState("");
    const [quantity, setQuantity] = useState("");
    const [description, setDescription] = useState("");

    const [editingId, setEditingId] = useState(null);

    const loadParts = async () => {
        try {
            const response = await api.get("/parts");
            setParts(response.data);
        } catch (error) {
            console.error(error);
            setError("Не удалось загрузить запчасти");
        }
    };

    useEffect(() => {
        loadParts();
    }, []);

    const resetForm = () => {
        setName("");
        setArticle("");
        setPrice("");
        setQuantity("");
        setDescription("");
        setEditingId(null);
    };

    const savePart = async (event) => {
        event.preventDefault();
        setError("");
        setMessage("");

        try {
            const data = {
                name,
                article,
                price: Number(price),
                quantity: Number(quantity),
                description
            };

            if (editingId) {
                await api.put(`/parts/${editingId}`, data);
                setMessage("Запчасть обновлена");
            } else {
                await api.post("/parts", data);
                setMessage("Запчасть добавлена");
            }

            resetForm();
            await loadParts();
        } catch (error) {
            console.error(error);
            setError(
                error.response?.data?.message ||
                "Не удалось сохранить запчасть"
            );
        }
    };

    const editPart = (part) => {
        setEditingId(part.id);
        setName(part.name);
        setArticle(part.article || "");
        setPrice(part.price);
        setQuantity(part.quantity);
        setDescription(part.description || "");
        setMessage("");
        setError("");
    };

    const deletePart = async (id) => {
        if (!window.confirm("Удалить эту запчасть?")) {
            return;
        }

        try {
            await api.delete(`/parts/${id}`);
            setMessage("Запчасть удалена");
            await loadParts();
        } catch (error) {
            console.error(error);
            setError(
                error.response?.data?.message ||
                "Не удалось удалить запчасть"
            );
        }
    };

    return (
        <div>
            <h1>Запчасти</h1>

            <form onSubmit={savePart}>
                <h2>{editingId ? "Редактирование запчасти" : "Новая запчасть"}</h2>

                <div>
                    <label>Название</label><br />
                    <input
                        value={name}
                        onChange={(event) => setName(event.target.value)}
                        required
                    />
                </div>

                <div>
                    <label>Артикул</label><br />
                    <input
                        value={article}
                        onChange={(event) => setArticle(event.target.value)}
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
                    <label>Количество на складе</label><br />
                    <input
                        type="number"
                        min="0"
                        value={quantity}
                        onChange={(event) => setQuantity(event.target.value)}
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
                    {editingId ? "Сохранить изменения" : "Добавить запчасть"}
                </button>

                {editingId && (
                    <button type="button" onClick={resetForm}>
                        Отмена
                    </button>
                )}
            </form>

            {message && <p>{message}</p>}
            {error && <p>{error}</p>}

            <h2>Склад запчастей</h2>

            <table border="1">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Название</th>
                        <th>Артикул</th>
                        <th>Цена</th>
                        <th>Количество</th>
                        <th>Описание</th>
                        <th>Действия</th>
                    </tr>
                </thead>

                <tbody>
                    {parts.map((part) => (
                        <tr key={part.id}>
                            <td>{part.id}</td>
                            <td>{part.name}</td>
                            <td>{part.article}</td>
                            <td>{part.price}</td>
                            <td>{part.quantity}</td>
                            <td>{part.description}</td>
                            <td>
                                <button onClick={() => editPart(part)}>
                                    Изменить
                                </button>

                                <button onClick={() => deletePart(part.id)}>
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

export default PartsPage;