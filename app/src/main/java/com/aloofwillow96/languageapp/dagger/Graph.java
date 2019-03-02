package com.aloofwillow96.languageapp.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {MainModule.class})
public interface Graph extends AppComponent {

	final class Initializer {
		public static Graph initialize(Application application) {
			return DaggerGraph.builder()
					.mainModule(new MainModule(application))
					.build();
		}
	}
}
