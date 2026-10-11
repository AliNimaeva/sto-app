import { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import Header from "./components/Header";
import OrdersPage from "./pages/OrdersPage";
import OrderPage from "./pages/OrderPage";
import ServicesPage from "./pages/ServicesPage";
import PartsPage from "./pages/PartsPage";
import UsersPage from "./pages/UsersPage";
import ReportsPage from "./pages/ReportsPage";
import CreateOrderPage from "./pages/CreateOrderPage";
import CheckOrderPage from "./pages/CheckOrderPage";
import HomePage from "./pages/HomePage";
import MyOrdersPage from "./pages/MyOrdersPage";

import api from "./api/api";

function App() {
    const token = localStorage.getItem("token");

    return (
        <BrowserRouter>
            {token && <Header />}

            <main>
            <Routes>
                <Route
                    path="/login"
                    element={
                        token
                            ? <Navigate to="/" />
                            : <LoginPage />
                    }
                />

                <Route
                    path="/"
                    element={
                        token
                            ? <HomePage />
                            : <HomePage />
                    }
               />

                <Route
                    path="/create-order"
                    element={<CreateOrderPage />}
                />

                <Route
                    path="/check-order"
                    element={<CheckOrderPage />}
                />

                <Route
                    path="/orders"
                    element={
                        token
                            ? <OrdersPage />
                            : <Navigate to="/login" />
                    }
                />

                <Route
                    path="/orders/:id"
                    element={
                        localStorage.getItem("token")
                            ? <OrderPage />
                            : <Navigate to="/login" />
                    }
                />

                <Route
                    path="/services"
                    element={token ? <ServicesPage /> : <Navigate to="/login" />}
                />

                <Route
                    path="/parts"
                    element={token ? <PartsPage /> : <Navigate to="/login" />}
                />

                <Route
                    path="/users"
                    element={token ? <UsersPage /> : <Navigate to="/login" />}
                />

                <Route
                    path="/reports"
                    element={token ? <ReportsPage /> : <Navigate to="/login" />}
                />

                <Route
                    path="/my-orders"
                    element={
                        token
                            ? <MyOrdersPage />
                            : <Navigate to="/login" />
                    }
                />


            </Routes>
            </main>
        </BrowserRouter>
    );
}

export default App;