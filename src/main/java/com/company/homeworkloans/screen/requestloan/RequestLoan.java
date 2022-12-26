package com.company.homeworkloans.screen.requestloan;

import com.company.homeworkloans.app.RequestService;
import com.company.homeworkloans.entity.Client;
import com.company.homeworkloans.entity.Loan;
import io.jmix.ui.Dialogs;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.app.inputdialog.InputDialog;
import io.jmix.ui.app.inputdialog.InputParameter;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.EntityComboBox;
import io.jmix.ui.component.TextField;
import io.jmix.ui.component.inputdialog.InputDialogAction;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Objects;

import static io.jmix.ui.icon.JmixIcon.PLAY;

@UiController("RequestLoanScreen")
@UiDescriptor("requestloan-screen.xml")
@EditedEntityContainer("clientDc")
@LookupComponent("form")
public class RequestLoan extends Screen {
    @Autowired
    private Dialogs dialogs;
    @Autowired
    RequestService requestService;
    @Autowired
    private Notifications notifications;
    @Autowired
    private UiComponents uiComponents;

    @Subscribe
    public void onAfterShow(BeforeShowEvent event) {
        dialogs.createInputDialog(this)
                .withCaption("Request Loan")
                .withParameters(
                        InputParameter.parameter("clientField")
                                .withField(() -> {
                                    EntityComboBox<Client> field = uiComponents
                                            .create(EntityComboBox.of(Client.class));
                                    field.setOptionsList(requestService.loadClients());
                                    field.setCaption("Client");
                                    field.setRequired(true);
                                    field.setWidthAuto();
                                    field.setHeightAuto();
                                    return field;
                                }),
                        InputParameter.bigDecimalParameter("amountField")
                                .withField(() -> {
                                    TextField<BigDecimal> field = uiComponents
                                            .create(TextField.TYPE_BIGDECIMAL);
                                    field.setCaption("Amount");
                                    field.setRequired(true);
                                    return field;
                                })
                ).withActions(
                        InputDialogAction.action("request")
                                .withCaption("Request")
                                .withValidationRequired(true)
                                .withIcon(PLAY)
                                .withHandler(actionEvent -> {
                                    InputDialog dialog = Objects.requireNonNull(actionEvent.getInputDialog());
                                    Client client = dialog.getValue("clientField");
                                    BigDecimal amount = dialog.getValue("amountField");

                                    Notifications.NotificationBuilder builder = notifications.create()
                                            .withType(Notifications.NotificationType.TRAY);
                                    if (!requestService.isValidClient(client)) {
                                        builder.withCaption("Firstname or lastname is wrong").show();
                                    } else if (!requestService.isValidAmount(amount)) {
                                        builder.withCaption("Wrong value of amount").show();
                                    } else {
                                        Loan loanDb = requestService.createLoan(client, amount);
                                        if (loanDb != null) {
                                            dialog.close(WINDOW_CLOSE_ACTION);
                                        }
                                    }
                                }),
                        InputDialogAction.action("cancel")
                                .withCaption("Cancel")
                                .withValidationRequired(true)
                                .withHandler(actionEvent -> {
                                    InputDialog dialog = Objects.requireNonNull(actionEvent.getInputDialog());
                                    dialog.close(WINDOW_CLOSE_ACTION);
                                })
                ).show();
    }

    @Subscribe("cancelBtn")
    public void onCancelBtnClick(Button.ClickEvent event) {
        close(StandardOutcome.CLOSE);
    }
}