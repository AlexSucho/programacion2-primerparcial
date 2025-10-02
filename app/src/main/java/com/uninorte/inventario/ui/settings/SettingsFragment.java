package com.uninorte.inventario.ui.settings;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import android.widget.TextView;

public class SettingsFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView tv = new TextView(requireContext());
        tv.setText("Ajustes");
        tv.setPadding(32,32,32,32);
        return tv;
    }
}
