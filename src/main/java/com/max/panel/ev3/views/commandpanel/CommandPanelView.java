package com.max.panel.ev3.views.commandpanel;

import com.max.panel.ev3.Application;
import com.max.panel.ev3.communication.CommunicationClient;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.max.panel.ev3.views.MainLayout;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.boot.SpringApplication;
import org.vaadin.artur.helpers.LaunchUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.helger.commons.mock.CommonsAssert.assertEquals;

@PageTitle("Command Panel")
@Route(value = "panel", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class CommandPanelView extends HorizontalLayout {


    // Klassen Variablen initalisieren
    private TextField command;
    private Button commitCommand;

    // Initialisieren der Variable und Wertezuweisung
    private String output = "Initializing session";
    private String sendCommand;
    private CommunicationClient client;

    public CommandPanelView() throws IOException {

        /**
         *  Programmierung des CommandPanels, definition und deklaration wichtiger UI Elemente.
         *  Dabei dient folgender Code nur als Schnittstelle.
         *
         *  @see 51-56. CommandPanelView.java
         * **/

        setMargin(true);
        VerticalLayout verticalLayout = new VerticalLayout();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button tryAgain = new Button("Try Again");
        tryAgain.addThemeVariants(ButtonVariant.LUMO_ERROR);
        tryAgain.setVisible(false);
        TextArea responses = new TextArea("Socket Response");
        responses.setReadOnly(true);
        responses.setWidth("80%");
        tryAgain.getElement().getStyle().set("margin-top", "2.rem");

        command = new TextField("Your command");
        commitCommand = new Button("Send");
        add(command, commitCommand, responses);
        setVerticalComponentAlignment(Alignment.END, command, commitCommand);

        Dialog dialog = new Dialog(buildSoundFunctionality());
        Button openDialog = new Button("Sound hochladen");
        openDialog.addThemeVariants(ButtonVariant.LUMO_SUCCESS);


        /*
         * Im folgenden wird der Socket erstellt. Dazu wird die initialisierte @client Variable instanziiert (erkennbar durch 'new' Keyword).
         *
         * @exception IOException muss aufgefangen werden, daher try catch
         * @see https://www.baeldung.com/java-socket
         *
         * @param ip
         * @param port
         * werden beide der Methode übergeben.
         *
         * Ein Port ist der Teil einer Netzwerk-Adresse, der die Zuordnung von TCP- und UDP-Verbindungen
         * und -Datenpaketen zu Server- und Client-Programmen durch Betriebssysteme bewirkt.
         * Zu jeder Verbindung dieser beiden Protokolle gehören zwei Ports,
         * je einer auf Seiten des Clients und des Servers. Wikipedia
         *
         * */

        client = new CommunicationClient();
        try {
            client.startConnection("192.168.0.52", 33334);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String response = null;


        /*
         *  Hier wird die Methode zum Senden der Commands aufgerufen und eine erste Nachricht mit dem Token
         *  @param conEv3 übergeben, welcher zur verifikation des client dient.
         *
         */
        try {
                response = client.sendMessage("conEv3");
                output = response.toString();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                responses.setValue("Error. Could not connect, please make sure your robot is on, connected and accessible. Click try again for a retry.");
                tryAgain.setVisible(true);
            }


        // Wenn beim ersten Versuch die Verbindung nicht geklappt hat, wird ein "Try Again" Button angezeigt.
            tryAgain.addClickListener(event -> {

                try {
                    String rs = client.sendMessage("conEv3");
                    responses.setValue(rs);
                    tryAgain.setVisible(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    responses.setValue("Error. Could not connect, please make sure your robot is on, connected and accessible. Click try again for a retry.");
            }

            });

            // Output wird in die TextArea geschrieben.
            responses.setValue(output);


            // Befehl wird gesendet und eine Antwort erhalten, welche anschließend als Wert der TextArea festgelegt wird.
            commitCommand.addClickListener(e -> {

                try {
                    sendCommand = null;
                    sendCommand = client.sendMessage(command.getValue());
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    responses.setValue(output + "No response. Retrying...");
                }

                output = output + sendCommand.toString();
                responses.setValue(output);

            });

            // Socket wird geschlossen, wenn Seite aktualisiert oder geschlossen wird.
            if (UI.getCurrent().isClosing() || !UI.getCurrent().isAttached()) {
                client.stopConnection();
            }

            openDialog.addClickListener(event -> {

                dialog.open();

            });

            horizontalLayout.add(command, tryAgain, commitCommand, openDialog);
            verticalLayout.add(horizontalLayout, responses);
            add(verticalLayout);
        }

    private VerticalLayout buildSoundFunctionality() {
        VerticalLayout verticalLayout = new VerticalLayout();

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);

        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = buffer.getInputStream(fileName);

            try {
                client.sendMessage("sound" + inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        upload.getElement().getStyle().set("margin-top", "2.rem");
        verticalLayout.add(upload);
        return verticalLayout;
    }
}