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

    private TextField command;
    private Button commitCommand;

    private String output = "Initializing session";
    private String sendCommand;
    private CommunicationClient client;

    public CommandPanelView() throws IOException {

        setMargin(true);

        VerticalLayout verticalLayout = new VerticalLayout();
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        Button tryAgain = new Button("Try Again");
        tryAgain.addThemeVariants(ButtonVariant.LUMO_ERROR);
        tryAgain.setVisible(false);

        TextArea responses = new TextArea("Socket Response");
        responses.setReadOnly(true);
        responses.setWidth("80%");

        tryAgain.getStyle().set("margin-top", "2.rem");


        client = new CommunicationClient();
        try {
            client.startConnection("192.168.0.52", 33334);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        String response = null;

            try {
                response = client.sendMessage("conEv3");
                output = response.toString();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                responses.setValue("Error. Could not connect, please make sure your robot is on, connected and accessible. Click try again for a retry.");
                tryAgain.setVisible(true);
            }

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


            responses.setValue(output);

            command = new TextField("Your command");
            commitCommand = new Button("Send");
            add(command, commitCommand, responses);
            setVerticalComponentAlignment(Alignment.END, command, commitCommand);

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


            if (UI.getCurrent().isClosing() || !UI.getCurrent().isAttached()) {
                client.stopConnection();
            }


            Dialog dialog = new Dialog(buildSoundFunctionality());
            Button openDialog = new Button("Sound hochladen");
            openDialog.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

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

        upload.getStyle().set("margin-top", "2.rem");
        verticalLayout.add(upload);
        return verticalLayout;
    }
}