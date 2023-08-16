package com.photo.editor.snapstudio.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.photo.editor.snapstudio.AdsUtils.FirebaseADHandlers.AdUtils;
import com.photo.editor.snapstudio.PhEditor.Model.Templates;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.Templates.frame.Adapter_grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TemplatesFragment extends Fragment {

    TextView textView, textView2;
    ImageView square_btn, potrait_btn, story_btn, post_btn, cover_btn, cover_p_btn, post_t_btn, header_btn, post_y_btn;
    boolean isDarkTheme;
    GridView grid;
    Adapter_grid adapter;
    Task<DocumentSnapshot> collageQuery;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    FirebaseFirestore fstore;
    ArrayList<Templates> TemplateItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_templates, container, false);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        textView = view.findViewById(R.id.txt);
        textView2 = view.findViewById(R.id.txt2);
        square_btn = view.findViewById(R.id.square_btn);
        potrait_btn = view.findViewById(R.id.potrait_btn);
        story_btn = view.findViewById(R.id.story_btn);
        post_btn = view.findViewById(R.id.post_btn);
        cover_btn = view.findViewById(R.id.cover_btn);
        cover_p_btn = view.findViewById(R.id.cover_p_btn);
        post_t_btn = view.findViewById(R.id.post_t_btn);
        header_btn = view.findViewById(R.id.header_btn);
        post_y_btn = view.findViewById(R.id.post_y_btn);
        grid = view.findViewById(R.id.gridView1);
//        changeTheme();
        isDarkTheme = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);
        AdUtils.showNativeAd(requireActivity(), view.findViewById(R.id.native_ad), false, isDarkTheme, false);
        showProgressDialog("Loading...");

        collageQuery = fstore.collection("Templates").document("Templates").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    TemplateItems = new ArrayList<>();
                    List<HashMap<String, String>> testlist = (List<HashMap<String, String>>) snapshot.getData().get("data");
                    for (int i = 0; i < testlist.size(); i++) {
                        Templates singleItem = new Templates();
                        singleItem.setImageUrl(testlist.get(i).get("imageUrl"));
                        TemplateItems.add(singleItem);
                    }
                    hideProgressDialog();
                    adapter = new Adapter_grid(requireActivity(), TemplateItems, "template");
//                    LinearLayoutManager bmanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//                    temprv.setLayoutManager(bmanager);
                    grid.setAdapter(adapter);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();

                Toast.makeText(requireActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void showProgressDialog(String msg) {

        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setTitle(null);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        try {
            if (progressDialog != null && !progressDialog.isShowing()) progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeTheme() {
        isDarkTheme = getActivity().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isDarkTheme", false);

        if (isDarkTheme) {
//            mainLayout.setBackgroundResource(R.drawable.darkbg);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView2.setTextColor(getResources().getColor(R.color.white));
            square_btn.setImageResource(R.drawable.square_dark);
            potrait_btn.setImageResource(R.drawable.potrait_dark);
            story_btn.setImageResource(R.drawable.story_dark);
            post_btn.setImageResource(R.drawable.post_dark);
            cover_btn.setImageResource(R.drawable.cover_dark);
            cover_p_btn.setImageResource(R.drawable.cover_p_dark);
            post_t_btn.setImageResource(R.drawable.post_t_dark);
            header_btn.setImageResource(R.drawable.header_dark);
            post_y_btn.setImageResource(R.drawable.post_y_dark);
        } else {
//            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            textView.setTextColor(getResources().getColor(R.color.blacktwo));
            textView2.setTextColor(getResources().getColor(R.color.blacktwo));
            square_btn.setImageResource(R.drawable.square);
            potrait_btn.setImageResource(R.drawable.potrait);
            story_btn.setImageResource(R.drawable.story);
            post_btn.setImageResource(R.drawable.post);
            cover_btn.setImageResource(R.drawable.cover);
            cover_p_btn.setImageResource(R.drawable.cover_p);
            post_t_btn.setImageResource(R.drawable.post_t);
            header_btn.setImageResource(R.drawable.header);
            post_y_btn.setImageResource(R.drawable.post_y);
        }
    }
}