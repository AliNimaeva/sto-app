import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/api";

function CreateOrderPage() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        clientName: "",
        clientPhone: "",
        brand: "",
        model: "",
        year: "",
        plateNumber: "",
        problemDescription: "",
    });

    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);
    const [orderNumber, setOrderNumber] = useState("");

    const handleChange = (event) => {
        const { name, value } = event.target;

        setForm((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        setError("");
        setSuccess(false);

        try {
            const response = await api.post("/orders", {
                ...form,
                year: Number(form.year),
            });

            setOrderNumber(response.data.number);
            setSuccess(true);
        } catch (error) {
            console.error(error);

            if (error.response?.data?.message) {
                setError(error.response.data.message);
            } else {
                setError("Не удалось создать заявку");
            }
        }
    };

    if (success) {
        return (
            <div>
                <h1>Заявка создана</h1>

                <div className="card">
                    <h2>Ваша заявка успешно принята</h2>

                    <p>
                        Номер заказа:
                        <strong> {orderNumber}</strong>
                    </p>

                    <p>
                        Сохраните этот номер. Он понадобится
                        для проверки статуса заказа.
                    </p>

                    <div>
                        <Link to="/check-order">
                            <button>Проверить заказ</button>
                        </Link>

                        <Link to="/">
                            <button>На главную</button>
                        </Link>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div>
            <h1>Создание заявки</h1>

            <p>
                Заполните данные автомобиля и опишите проблему.
            </p>

            <form onSubmit={handleSubmit}>
                <div>
                    <label>Ваше имя</label>
                    <input
                        type="text"
                        name="clientName"
                        value={form.clientName}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div>
                    <label>Телефон</label>
                    <input
                        type="tel"
                        name="clientPhone"
                        value={form.clientPhone}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div>
                    <label>Марка автомобиля</label>
                    <input
                        type="text"
                        name="brand"
                        value={form.brand}
                        onChange={handleChange}
                        placeholder="Например, Toyota"
                        required
                    />
                </div>

                <div>
                    <label>Модель автомобиля</label>
                    <input
                        type="text"
                        name="model"
                        value={form.model}
                        onChange={handleChange}
                        placeholder="Например, Camry"
                        required
                    />
                </div>

                <div>
                    <label>Год выпуска</label>
                    <input
                        type="number"
                        name="year"
                        value={form.year}
                        onChange={handleChange}
                        min="1950"
                        required
                    />
                </div>

                <div>
                    <label>Государственный номер</label>
                    <input
                        type="text"
                        name="plateNumber"
                        value={form.plateNumber}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div>
                    <label>Описание проблемы</label>
                    <textarea
                        name="problemDescription"
                        value={form.problemDescription}
                        onChange={handleChange}
                        rows="6"
                        placeholder="Опишите неисправность автомобиля"
                        required
                    />
                </div>

                {error && (
                    <p className="error">
                        {error}
                    </p>
                )}

                <button type="submit">
                    Создать заявку
                </button>
            </form>

            <p>
                <Link to="/">Вернуться на главную</Link>
            </p>
        </div>
    );
}

export default CreateOrderPage;

