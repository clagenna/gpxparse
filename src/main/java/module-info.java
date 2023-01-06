/**
 * @author claudio
 *
 */
module gpxparse {
  exports sm.clagenna.gpxparse.javafx;
  

  requires javafx.controls;
  requires javafx.fxml;
  requires transitive javafx.graphics;
  requires javafx.base;
  requires javafx.web;
  requires javafx.media;
  requires org.controlsfx.controls;
  requires lombok;
  requires java.sql;
  requires com.jfoenix;
  requires java.desktop;
  requires org.apache.logging.log4j;
  requires commons.cli;


  opens sm.clagenna.gpxparse.javafx to javafx.fxml;

}
