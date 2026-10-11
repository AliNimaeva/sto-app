import { Link, useNavigate } from "react-router-dom";

function Header() {
    const navigate = useNavigate();

    const token = localStorage.getItem("token");
    const name = localStorage.getItem("name");
    const role = localStorage.getItem("role");

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("email");
        localStorage.removeItem("name");
        localStorage.removeItem("role");

        navigate("/login");
    };

    return (
        <header>
            <h1>
                <Link to="/">СТО</Link>
            </h1>

            {!token && (
                <nav>
                    <Link to="/">Главная</Link>
                    <Link to="/create-order">Создать заявку</Link>
                    <Link to="/check-order">Проверить заказ</Link>
                    <Link to="/login">Войти</Link>
                </nav>
            )}

            {token && (
                <>
                    <nav>
                        <Link to="/">Главная</Link>

                        {role === "ADMIN" && (
                            <>
                                <Link to="/orders">Заказы</Link>
                                <Link to="/services">Услуги</Link>
                                <Link to="/parts">Запчасти</Link>
                            </>
                        )}

                        {role === "MASTER" && (
                            <Link to="/my-orders">Мои заказы</Link>
                        )}

                        {role === "MANAGER" && (
                            <>
                                <Link to="/orders">Заказы</Link>
                                <Link to="/users">Пользователи</Link>
                                <Link to="/reports">Отчёты</Link>
                            </>
                        )}
                    </nav>

                    <div>
                        <span>
                            {name} ({role})
                        </span>

                        <button onClick={logout}>
                            Выйти
                        </button>
                    </div>
                </>
            )}
        </header>
    );
}

export default Header;

