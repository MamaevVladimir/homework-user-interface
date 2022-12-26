package com.company.homeworkloans.screen.loan;

import com.company.homeworkloans.app.RequestService;
import com.company.homeworkloans.entity.Loan;
import io.jmix.ui.Notifications;
import io.jmix.ui.UiComponents;
import io.jmix.ui.action.Action;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.component.TextField;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Function;

@UiController("LoanApproval.browse")
@UiDescriptor("loan-approval-browse.xml")
@LookupComponent("table")
public class LoanApprovalBrowse extends StandardLookup<Loan> {

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private GroupTable<Loan> table;

    @Autowired
    private RequestService requestService;
    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        table.groupByColumns("requestDate");
    }

    @Subscribe
    protected void onInit(InitEvent event) {
        table.addGeneratedColumn("Client age", loan -> {
            TextField<String> field = uiComponents.create(TextField.NAME);
            Function<Loan, String> function = entity -> String.valueOf(LocalDate.now().getYear() - loan.getClient().getBirthDate().getYear());
            field.setCaption("Client age");
            field.setValue(function.apply(loan));

            return field;
        });

        Objects.requireNonNull(table.getColumn("client"))
                .setValueProvider(entity -> entity.getClient().getFirstName() + " " + entity.getClient().getLastName());

    }

    @Subscribe("table.approve")
    public void onTableApprove(Action.ActionPerformedEvent event) {
        Loan loan = Objects.requireNonNull(table.getSingleSelected());
        requestService.setApprovedStatus(loan.getId());
        requestService.deleteLoan(table.getSingleSelected().getId());

        notifications.create()
                .withType(Notifications.NotificationType.TRAY)
                .withCaption("Approved").show();
    }

    @Subscribe("table.reject")
    public void onTableReject(Action.ActionPerformedEvent event) {
        Loan loan = Objects.requireNonNull(table.getSingleSelected());
        requestService.setRejectedStatus(loan.getId());
        requestService.deleteLoan(table.getSingleSelected().getId());

        notifications.create()
                .withType(Notifications.NotificationType.TRAY)
                .withCaption("Rejected").show();
    }
}
