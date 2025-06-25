package com.salon.Model;

import com.salon.Views.ViewFactory;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final DatabaseConnection databaseDriver;

    private Model() {
        this.viewFactory = new ViewFactory();
        this.databaseDriver = new DatabaseConnection();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public DatabaseConnection getDatabaseDriver() {
        return databaseDriver;
    }
}