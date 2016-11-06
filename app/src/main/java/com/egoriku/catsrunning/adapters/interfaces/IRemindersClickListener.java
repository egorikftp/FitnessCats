package com.egoriku.catsrunning.adapters.interfaces;

public interface IRemindersClickListener {
    void onDeleteReminderClick(int id, int position, int typeReminder);
    void onTimeReminderClick(int id, long dateReminder, int typeReminder, int position);
    void onDateReminderClick(int id, long dateReminder, int typeReminder, int position);
    void onSwitcherReminderClick(int id, long dateReminder, int typeReminder, int position, boolean isChecked);
}
