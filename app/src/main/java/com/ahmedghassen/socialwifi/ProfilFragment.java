package com.ahmedghassen.socialwifi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ProfilFragment extends Fragment {
    int cc;

    public ProfilFragment() {
        // Required empty public constructor
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);;
        Bundle bundle = this.getArguments();
        cc = bundle.getInt("container");

        Button update = (Button)view.findViewById(R.id.update);
        update.setOnClickListener((v -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            UpdateFragment pf = new UpdateFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putInt("container", cc);

            pf.setArguments(bundle2);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(cc, pf, "CF");
            transaction.commit();
        }));

        return view;
    }

}
