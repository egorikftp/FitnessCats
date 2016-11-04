package com.egoriku.catsrunning.adapters.interfaces;

public interface IRemindersClickListener {
    void onDeleteReminderClick(int id, int position, int typeReminder);
    void onRecyclerViewClickEvent(int id, long dateReminder, int typeReminder, int position);
}
