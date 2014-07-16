package owuor.f8th.dialog;

import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;

import owuor.f8th.R;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class F8thDialog extends Dialog{
	Context mContext;
	Dialog alertDialog;
	Button btnDialogOk;
	TextView dialogMessage;
	
	public F8thDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		alertDialog = new Dialog(mContext);
		
		alertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(mContext, R.layout.f8th_dialog,null);
		alertDialog.setContentView(view);
		
		btnDialogOk = (Button)view.findViewById(R.id.btnDialogOk);
		dialogMessage = (TextView)view.findViewById(R.id.dialogMessage);
		
		Log.i("ShareDialog","share dialog started");
	}//end of constructor
	
	public boolean alertDialog(String message){
		dialogMessage.setText(message);
		dialogMessage.setVisibility(View.VISIBLE);
		dialogMessage.setTextColor(Color.RED);
		btnDialogOk.setVisibility(View.VISIBLE);
		
		btnDialogOk.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
		WindowManager.LayoutParams wndw = alertDialog.getWindow().getAttributes();
		wndw.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wndw.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		alertDialog.show();
		
		return true;
	}//end of method
	
	public void alertDialog(String message,int color){
		dialogMessage.setText(message);
		dialogMessage.setVisibility(View.VISIBLE);
		dialogMessage.setTextColor(color);
		btnDialogOk.setVisibility(View.VISIBLE);
		
		btnDialogOk.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
		WindowManager.LayoutParams wndw = alertDialog.getWindow().getAttributes();
		wndw.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wndw.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		alertDialog.show();
	}
		
		public void dismissDialog(){
			if(alertDialog.isShowing()){
				alertDialog.dismiss();
			}
		}//end of method dismissDialog()
	
	
}//END OF CLASS ShareDialog