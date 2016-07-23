package xyz.aungpyaephyo.padc.myanmarattractions.fragments;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.aungpyaephyo.padc.myanmarattractions.R;
import xyz.aungpyaephyo.padc.myanmarattractions.data.models.AttractionModel;
import xyz.aungpyaephyo.padc.myanmarattractions.data.models.UserModel;
import xyz.aungpyaephyo.padc.myanmarattractions.utils.ValidationUtils;
import xyz.aungpyaephyo.padc.myanmarattractions.views.PasswordVisibilityListener;

/**
 * Created by aung on 7/15/16.
 */
public class LoginFragment extends Fragment
    implements Validator.ValidationListener {

    @BindView(R.id.iv_background)
    ImageView ivBackground;

    @BindView(R.id.lbl_login_title)
    TextView tvLoginTitle;

    @BindView(R.id.tv_recover_password)
    TextView tvRecoverPassword;

    @BindView(R.id.tv_navigate_to_register)
    TextView tvNavigatetoRegister;

    @Order(1) @NotEmpty(messageResId = R.string.error_missing_email) @Email(messageResId = R.string.error_email_is_not_valid)
    @BindView(R.id.et_email)
    EditText etEmail;

    @Order(2) @NotEmpty(messageResId = R.string.error_missing_password)
    @BindView(R.id.et_password)
    EditText etPassword;

    // Saripaar Validation
    Validator mValidator;

    private ControllerLogin mControllerLogin;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ControllerLogin) {
            mControllerLogin = (ControllerLogin) context;
        } else {
            throw new RuntimeException("Activity is not implementing required controller for LoginFragment.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        mValidator.setValidationMode(Validator.Mode.BURST);

        ButterKnife.bind(this, rootView);

        String randomBackgroundImgUrl = AttractionModel.getInstance().getRandomAttractionImage();
        if (randomBackgroundImgUrl != null) {
            Glide.with(ivBackground.getContext())
                    .load(randomBackgroundImgUrl)
                    .centerCrop()
                    .placeholder(R.drawable.drawer_background)
                    .error(R.drawable.drawer_background)
                    .into(ivBackground);
        }

        tvLoginTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getX() >= tvLoginTitle.getRight() - tvLoginTitle.getTotalPaddingRight()) {
                        getActivity().onBackPressed();
                        return true;
                    }
                }
                return true;
            }
        });

        tvRecoverPassword.setText(Html.fromHtml(getString(R.string.lbl_recover_password)));
        tvNavigatetoRegister.setText(Html.fromHtml(getString(R.string.lbl_navigate_to_register)));
        tvLoginTitle.setText(Html.fromHtml(getString(R.string.lbl_login_title)));
        etPassword.setOnTouchListener(new PasswordVisibilityListener());

        return rootView;
    }

    @OnClick(R.id.btn_login)
    public void OnLoginClicked(Button btnLogin)
    {
        mValidator.validate();
    }

    //region Validation Listener
    @Override
    public void onValidationSucceeded() {
        mControllerLogin.onLogin(etEmail.getText().toString(), etPassword.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
    //endregion

    //region Interface Declaration
    public interface ControllerLogin {
        void onLogin(String email, String password);
    }
    //endregion
}
