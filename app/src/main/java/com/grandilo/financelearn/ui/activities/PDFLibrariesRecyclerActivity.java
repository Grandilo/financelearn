package com.grandilo.financelearn.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.grandilo.financelearn.R;
import com.grandilo.financelearn.ui.adapters.LibrariesAdapter;
import com.grandilo.financelearn.utils.FinanceLearningConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ugo
 */

public class PDFLibrariesRecyclerActivity extends AppCompatActivity {

    private List<HashMap<String, String>> pdfLibraries = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libraries);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        HashMap<String, String> ebankingPdf = new HashMap<>();
        ebankingPdf.put(FinanceLearningConstants.FILE_NAME,"e-banking.pdf");
        ebankingPdf.put("e-banking.pdf", "https://firebasestorage.googleapis.com/v0/b/financelearn-dffeb.appspot.com/o/Libraries%2Fe-bank.pdf?alt=media&token=8ebda546-2432-4d08-8050-217087fcec9f");

        HashMap<String, String> compliancePdf = new HashMap<>();

        compliancePdf.put(FinanceLearningConstants.FILE_NAME,"EMEAAP AC - Complaince Agent training - EN V1 (2).pdf");
        compliancePdf.put("EMEAAP AC - Complaince Agent training - EN V1 (2).pdf",
                "https://firebasestorage.googleapis.com/v0/b/financelearn-dffeb.appspot.com/o/Libraries%2FEMEAAP%20AC%20-%20Complaince%20Agent%20training%20-%20EN%20V1%20(2).pdf?alt=media&token=32fa54d4-8cc4-4be4-a218-dd5a8081fc1f");

        HashMap<String, String> loansAndCredit = new HashMap<>();
        loansAndCredit.put(FinanceLearningConstants.FILE_NAME,"Loans and Credit.pdf");
        loansAndCredit.put("Loans and Credit.pdf",
                "https://firebasestorage.googleapis.com/v0/b/financelearn-dffeb.appspot.com/o/Libraries%2FLoans%20and%20Credit.pdf?alt=media&token=3d2e016b-f4cf-4891-9db7-0d22c7603115");

        pdfLibraries.add(ebankingPdf);
        pdfLibraries.add(compliancePdf);
        pdfLibraries.add(loansAndCredit);

        RecyclerView librariesRecyclerView = (RecyclerView) findViewById(R.id.libraries_recycler_view);
        LibrariesAdapter librariesAdapter = new LibrariesAdapter(this, pdfLibraries);
        librariesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        librariesRecyclerView.setAdapter(librariesAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
