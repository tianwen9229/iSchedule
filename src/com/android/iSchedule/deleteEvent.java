package com.android.iSchedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class deleteEvent {
	public static void delEvent(Context context, int eventId) {
		iScheduleDB dbHelper = 
		new AlertDialog.Builder(context).setTitle("请选择")
		.setIcon(android.R.drawable.ic_dialog_info)                
		.setSingleChoiceItems(new String[] {"删除这个活动主题一致的所有活动","仅删除当前活动" }, 0, 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		     }
		  }
		)
		.setNegativeButton("取消", null)
		.show();
	}
}