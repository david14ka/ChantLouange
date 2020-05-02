package com.davidkazad.chantlouange.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (Prefs.getBoolean("Theme",false)){
            getActivity().setTheme(R.style.MyAppTheme2);
        }else {

            getActivity().setTheme(R.style.MyAppTheme);
        }*/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void openHelp() {
        openBrowser("help");
    }
    protected void openBrowser(String bookid) {
        String url1 = "https://alwaysdata.tclcantiques.net/?"+bookid;
        String url = "https://14ka135.wixsite.com/website/?"+bookid;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }
}
