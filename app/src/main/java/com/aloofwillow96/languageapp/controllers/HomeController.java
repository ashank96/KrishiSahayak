package com.aloofwillow96.languageapp.controllers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aloofwillow96.languageapp.KrishiSahayakApplication;
import com.aloofwillow96.languageapp.R;
import com.aloofwillow96.languageapp.ViewUtils;
import com.aloofwillow96.languageapp.activities.BaseActivity;
import com.aloofwillow96.languageapp.adapters.HomeAdapter;
import com.aloofwillow96.languageapp.contracts.HomeContract;
import com.aloofwillow96.languageapp.databinding.ConductorHomeBinding;
import com.aloofwillow96.languageapp.models.CropModel;
import com.aloofwillow96.languageapp.models.SoilForeCastResponse;
import com.aloofwillow96.languageapp.models.WeatherResponse;
import com.aloofwillow96.languageapp.presenters.HomePresenter;
import com.hannesdorfmann.mosby3.mvp.conductor.MvpController;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;


public class HomeController extends MvpController<HomeContract.View,HomePresenter>
		implements HomeContract.View,LocationListener {
	ConductorHomeBinding conductorHomeBinding;
	HomeAdapter homeAdapter;
	Location currentLocation;

	@NonNull
	@Override
	protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
		conductorHomeBinding = DataBindingUtil.inflate(inflater, R.layout.conductor_home, container, false);
		init();
		return conductorHomeBinding.getRoot();
	}

	@SuppressLint("MissingPermission")
	@Override
	protected void onAttach(@NonNull View view) {
		super.onAttach(view);
		if(permissionsGranted()) {
			currentLocation = ((BaseActivity) getActivity()).getLocationManager()
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			getPresenter().loadWeatherData(currentLocation);
			getPresenter().loadSoilData(currentLocation);
		}
		requestLocationService();

	}

	private void init() {
		homeAdapter = new HomeAdapter();
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
		conductorHomeBinding.recyclerView.setLayoutManager(gridLayoutManager);
		conductorHomeBinding.recyclerView.setAdapter(homeAdapter);
		homeAdapter.updateList(getItemList());
	}

	private void requestLocationService() {
		if (!permissionsGranted()) {
			requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
		} else {
			requestForLocationUpdates();
		}
}

	@SuppressLint("MissingPermission")
	private void requestForLocationUpdates() {
		((BaseActivity) getActivity()).getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
		((BaseActivity) getActivity()).getLocationManager().requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1, this);
	}

	@SuppressLint("MissingPermission")
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case 1:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					ViewUtils.makeToastShort(getActivity(), "Permission Granted");
					requestForLocationUpdates();
				} else {
					ViewUtils.makeToastShort(getActivity(), "Permission denied");
				}
		}
	}


	@Override
	public void onLocationChanged(Location location) {
		currentLocation=location;
		getPresenter().loadWeatherData(currentLocation);
		getPresenter().loadSoilData(currentLocation);
		Log.i("Location", location.getLatitude() + " " + location.getLongitude());
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i("Location", provider + " status changed");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i("Location", provider + " enabled");
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i("Location", provider + " disabled");
	}

	private boolean permissionsGranted() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
					getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}


	private List<CropModel> getItemList() {
		List<CropModel> cropModelList = new ArrayList<>();
		cropModelList.add(new CropModel(R.drawable.ic_farmer, ViewUtils.getString(R.string.become_a_member), ViewUtils.getString(R.string.join_us)));
		cropModelList.add(new CropModel(R.drawable.ic_crop, ViewUtils.getString(R.string.crop_info), ViewUtils.getString(R.string.check_the_info_about_crops)));
		cropModelList.add(new CropModel(R.drawable.ic_soil, ViewUtils.getString(R.string.soil_info), ViewUtils.getString(R.string.check_soil_health_info_here)));
		cropModelList.add(new CropModel(R.drawable.ic_insecticide, ViewUtils.getString(R.string.pest_control), ViewUtils.getString(R.string.natural_insecticides_and_pestisides)));
		cropModelList.add(new CropModel(R.drawable.ic_insurance, ViewUtils.getString(R.string.insurance), ViewUtils.getString(R.string.crop_security)));
		cropModelList.add(new CropModel(R.drawable.ic_globe, ViewUtils.getString(R.string.important_links), ViewUtils.getString(R.string.find_loads_of_resources)));
		cropModelList.add(new CropModel(R.drawable.ic_question, ViewUtils.getString(R.string.faq), ViewUtils.getString(R.string.ask_us)));
		cropModelList.add(new CropModel(R.drawable.ic_about_us, ViewUtils.getString(R.string.about_us), ViewUtils.getString(R.string.ask_any_queries)));
		return cropModelList;
	}

	@NonNull
	@Override
	public HomePresenter createPresenter() {
		return KrishiSahayakApplication.getInstance().getComponent().getPresenter();
	}

	@Override
	public void updateWeatherResponse(WeatherResponse weatherResponse) {
		Log.i("Weather Data",weatherResponse.getCityName());
		ViewUtils.makeToastShort(getActivity(),weatherResponse.getCityName());
	}

	@Override
	public void updateSoilResponse(SoilForeCastResponse soilResponse) {
		Log.i("Soil Data",soilResponse.getData().get(0).getValidDate());
	}
}
