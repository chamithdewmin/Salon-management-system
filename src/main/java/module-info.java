module com.savingRate.SavingRate {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // ✅ Add these to fix the read errors
    requires static org.apache.poi.poi;
    requires static org.apache.poi.ooxml;
    requires mysql.connector.j;
    requires itextpdf;

    opens com.salon.Model to javafx.base;
    opens com.salon.Controller to javafx.fxml;

    exports com.salon;
    opens com.salon.Views to javafx.fxml;
    opens com.salon.Utils to javafx.base, javafx.fxml;
}
