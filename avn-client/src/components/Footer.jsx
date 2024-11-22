import React from 'react';

function Footer() {
    const currentYear = new Date().getFullYear();

    return (
        <footer className="bg-light py-4 mt-auto">
            <div className="container">
                <div className="row align-items-center justify-content-between">
                    <div className="col-auto">
                        <p className="mb-0">
                            Alto Vale News &copy; {currentYear}
                        </p>
                    </div>
                    <div className="col-auto">
                        <p className="text-muted small mb-0">
                            Desenvolvido por AJMV Tech Ltda.
                        </p>
                    </div>
                </div>
            </div>
        </footer>
    );
}

export default Footer;