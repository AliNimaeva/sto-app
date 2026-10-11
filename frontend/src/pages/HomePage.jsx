import { Link } from "react-router-dom";

function HomePage() {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    const name = localStorage.getItem("name");

    // Публичная главная страница
    if (!token) {
        return (
            <div>
                <h1>СТО</h1>

                <p>
                    Автосервис. Создайте заявку на ремонт автомобиля
                    или проверьте статус существующего заказа.
                </p>

                <div className="card">
                    <h2>Новая заявка</h2>
                    <p>
                        Оставьте информацию об автомобиле и опишите проблему.
                    </p>
                    <Link to="/create-order">
                        <button>Создать заявку</button>
                    </Link>
                </div>

                <div className="card">
                    <h2>Проверка заказа</h2>
                    <p>
                        Узнайте статус ремонта по номеру заказа.
                    </p>
                    <Link to="/check-order">
                        <button>Проверить заказ</button>
                    </Link>
                </div>

                <div className="card">
                    <h2>Для сотрудников</h2>
                    <p>
                        Войдите в систему для работы с заказами.
                    </p>
                    <Link to="/login">
                        <button>Войти</button>
                    </Link>
                </div>
            </div>
        );
    }

    // Главная для сотрудников
    return (
        <div>
            <h1>Главная</h1>
            <p>
                Добро пожаловать, <strong>{name}</strong>!
            </p>

            {role === "ADMIN" && (
                <>
                    <div className="card">
                        <h2>Работа с заказами</h2>
                        <p>
                            Просмотр заявок, назначение мастеров
                            и управление ремонтом.
                        </p>
                        <Link to="/orders">
                            <button>Открыть заказы</button>
                        </Link>
                    </div>

                    <div className="card">
                        <h2>Справочники</h2>
                        <Link to="/services">
                            <button>Услуги</button>
                        </Link>
                        <Link to="/parts">
                            <button>Запчасти</button>
                        </Link>
                    </div>
                </>
            )}

            {role === "MASTER" && (
                <div className="card">
                    <h2>Мои заказы</h2>
                    <p>
                        Просматривайте назначенные вам заказы,
                        проводите диагностику и выполняйте ремонт.
                    </p>
                    <Link to="/my-orders">
                        <button>Открыть мои заказы</button>
                    </Link>
                </div>
            )}

            {role === "MANAGER" && (
                <>
                    <div className="card">
                        <h2>Заказы</h2>
                        <p>Просмотр заказов и контроль оплаты.</p>
                        <Link to="/orders">
                            <button>Открыть заказы</button>
                        </Link>
                    </div>

                    <div className="card">
                        <h2>Пользователи</h2>
                        <p>Управление сотрудниками системы.</p>
                        <Link to="/users">
                            <button>Пользователи</button>
                        </Link>
                    </div>

                    <div className="card">
                        <h2>Отчёты</h2>
                        <p>
                            Отчёты по заказам и мастерам
                            за выбранный период.
                        </p>
                        <Link to="/reports">
                            <button>Открыть отчёты</button>
                        </Link>
                    </div>
                </>
            )}
        </div>
    );
}

export default HomePage;
