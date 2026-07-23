package com.example.footstaats;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.footstaats.ui.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void login_camposVacios_muestraErrorCorreo() {
        // Dejar campos vacíos y presionar Entrar
        Espresso.onView(ViewMatchers.withId(R.id.btnEntrar))
                .perform(ViewActions.click());

        // Verificar que el error de correo es visible
        Espresso.onView(ViewMatchers.withId(R.id.tvErrorCorreo))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void login_correoInvalido_muestraError() {
        Espresso.onView(ViewMatchers.withId(R.id.etCorreo))
                .perform(ViewActions.typeText("correo_invalido"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.btnEntrar))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.tvErrorCorreo))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}