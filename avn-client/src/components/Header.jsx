import React from "react";
import MyNavbar from "./Navbar";

function Header() {

    return (
        <header>
            <nav className="navbar navbar-expand-lg navbar-light nav-custom">
                <div className="header-container">
                    <MyNavbar />
                </div>
            </nav>
        </header>
    );
}

export default Header;