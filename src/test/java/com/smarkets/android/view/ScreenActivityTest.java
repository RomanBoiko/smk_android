package com.smarkets.android.view;

import org.junit.Before;
import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.widget.Button;
import android.widget.TextView;

import com.smarkets.android.ScreenActivity;
import com.smarkets.android.R;

public class ScreenActivityTest extends ActivityInstrumentationTestCase2<ScreenActivity> {

	private ScreenActivity activity;

	public ScreenActivityTest() {
		super(ScreenActivity.class);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		activity = getActivity();
	}

	@Test
	public void testStartAuthorizedActivityAfterLogin() throws Exception {
		TextView loginInput = (TextView) activity.findViewById(R.id.txtUname);
		TextView passwordInput = (TextView) activity.findViewById(R.id.txtPwd);
		ViewAsserts.assertOnScreen(activity.getWindow().getDecorView(), loginInput);
		ViewAsserts.assertOnScreen(activity.getWindow().getDecorView(), passwordInput);
		
		Button view = (Button) activity.findViewById(R.id.btnLogin);
		TouchUtils.clickView(this, view);

		TextView cashView = (TextView) activity.findViewById(R.id.cash);
		ViewAsserts.assertOnScreen(activity.getWindow().getDecorView(), cashView);
	}

}
