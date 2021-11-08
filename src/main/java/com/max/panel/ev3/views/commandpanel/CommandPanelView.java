package com.max.panel.ev3.views.commandpanel;

import com.max.panel.ev3.Application;
import com.max.panel.ev3.communication.CommunicationClient;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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


    public CommandPanelView() {
        {


            setMargin(true);

            TextField responses = new TextField("Socket Response");
            responses.setReadOnly(true);

            command = new TextField("Your command");
            commitCommand = new Button("Send");
            add(command, commitCommand, responses);
            setVerticalComponentAlignment(Alignment.END, command, commitCommand);
            commitCommand.addClickListener(e -> {

                CommunicationClient client = new CommunicationClient();
                try {
                    client.startConnection("169.254.100.220", 6666);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                String response = null;
                try {
                    response = client.sendMessage("hello server");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                assertEquals("hello client", response);

            });
        }

    }

}