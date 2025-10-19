package com.example.assignmentpod.ui.tab.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.assignmentpod.R;

public class ProductDetailFragment extends Fragment {
    private NavController navController;

    public ProductDetailFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            int productId = args.getInt("productId", 0);
            // Load data by API
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(this);


    }

    public void navigateToPayment(int amount) {
        // TODO: Implement payment navigation when needed
        // This method is currently disabled as PaymentFragment is not in navigation graph
        /*
        if (navController != null) {
            Bundle paymentBundle = new Bundle();
            paymentBundle.putInt("amount", amount);
            navController.navigate(R.id.action_productDetailFragment_to_paymentFragment, paymentBundle);
        }
        */
    }
}