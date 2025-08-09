package com.simats.bloodbuddy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.button.MaterialButton;

public class DietTypeDialogFragment extends DialogFragment {
    private RadioGroup dietTypeGroup;
    private TextView dietTypeError;
    private MaterialButton continueButton;
    private MaterialButton cancelButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_diet_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        dietTypeGroup = view.findViewById(R.id.dietTypeGroup);
        dietTypeError = view.findViewById(R.id.dietTypeError);
        continueButton = view.findViewById(R.id.continueButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        // Set up continue button click listener
        continueButton.setOnClickListener(v -> {
            int selectedId = dietTypeGroup.getCheckedRadioButtonId();
            
            if (selectedId == -1) {
                // No radio button is selected
                dietTypeError.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Please select a diet type", Toast.LENGTH_SHORT).show();
            } else {
                // Get the selected diet type
                String dietType = "";
                if (selectedId == R.id.veganRadio) {
                    dietType = "vegan";
                } else if (selectedId == R.id.vegetarianRadio) {
                    dietType = "vegetarian";
                } else if (selectedId == R.id.nonVegRadio) {
                    dietType = "non_vegetarian";
                }

                // Navigate to SelectDayActivity with the selected diet type
                Intent intent = new Intent(getActivity(), SelectDayActivity.class);
                intent.putExtra("diet", dietType);
                startActivity(intent);
                dismiss();
            }
        });

        // Set up cancel button click listener
        cancelButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
} 