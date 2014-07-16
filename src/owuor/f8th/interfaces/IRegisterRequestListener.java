package owuor.f8th.interfaces;

public interface IRegisterRequestListener{
	
	public void onSignInRequested(String email,String password);
	public void onSignUpRequested(String email,String fname,String lname,String gender,String country,String password);
	public void onSaveProfileRequested(String about,String why,String desire);
	public void onForgotpasswordRequested(String email);
	
}
