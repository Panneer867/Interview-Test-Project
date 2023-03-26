package com.interview.test.controller;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.interview.test.entity.Location;
import com.interview.test.entity.Websites;
import com.interview.test.model.Message;
import com.interview.test.model.TableDetails;
import com.interview.test.model.UpdateLocation;
import com.interview.test.repository.LocationRepository;
import com.interview.test.repository.WebsiteRepository;

@Controller
public class HomeController {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private WebsiteRepository websiteRepository;

	@GetMapping("/home")
	public String home(Model model) {

		model.addAttribute("table", new TableDetails());
		return "home";
	}

	@GetMapping("/updatedLocations")
	public String updated(Model model, HttpSession session) {

		List<String> cities = locationRepository.findAll().stream().map(Location::getCityName)
				.collect(Collectors.toList());
		if (cities.size() == 0) {

			return "redirect:/home";
		}
		model.addAttribute("locations", cities);
		session.setAttribute("message", new Message("Cities Updated Successfully !", "success"));
		return "updated_location";
	}

	@PostMapping("/home")
	public String getLocations(@ModelAttribute("table") TableDetails TableDetails, HttpSession session, Model model) {

		if (TableDetails.getTableName().equals("interview.geography_columns")
				&& TableDetails.getFirstColumn().equals("id") && TableDetails.getSecondColumn().equals("city_name")) {

			List<String> cities = locationRepository.findAll().stream().map(Location::getCityName).distinct()
					.collect(Collectors.toList());

			model.addAttribute("locations", cities);

		} else {
			session.setAttribute("message", new Message("Please Provide Valid Details !", "danger"));
			return "redirect:/home";
		}

		return "update_location";
	}

	@PostMapping("/update/cities")
	public String updateLocations(@RequestBody UpdateLocation updateLocationData) {

		List<String> locations = updateLocationData.getLocation();
		List<String> updateLocations = updateLocationData.getUpdateLocation();

		for (int i = 0; i < locations.size(); i++) {
			if (updateLocations.get(i).length() != 0) {
				List<Location> loc = locationRepository.findByCityName(locations.get(i));
				for (Location locc : loc) {
					locc.setCityName(updateLocations.get(i));
					locationRepository.save(locc);
				}
			}
		}
		return "redirect:/updatedLocations";
	}

	@GetMapping("/updateLocations")
	public String back(Model model, HttpSession session) {

		List<String> cities = locationRepository.findAll().stream().map(Location::getCityName).distinct()
				.collect(Collectors.toList());

		model.addAttribute("locations", cities);
		session.setAttribute("message", new Message("Cities Updated Successfully !", "success"));
		return "update_location";
	}

	@GetMapping("/websiteCheck")
	public String websiteCheck(Model model, HttpSession session) {

		model.addAttribute("websites", websiteRepository.findAll());

		List<Websites> wb = websiteRepository.findAll();

		if (wb.size() != 0) {
			for (Websites ws : wb) {

				String wbs = ws.getWebsite();

				String[] www = wbs.split("\\.");
				if (www[0].equals("www") || www[0].equals("http://www") || www[0].equals("https://www")) {

					if (www[0].equals("www")) {
						wbs = "https://" + wbs;
					}

					try {
						URL url = new URL(wbs);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						int responseCode = connection.getResponseCode();
						if (responseCode == HttpURLConnection.HTTP_OK) {

							ws.setStatus("SUCCESS");
						} else {
							ws.setStatus("FAILURE");
						}
						connection.disconnect();
					} catch (Exception e) {
						System.out.println("Error: " + e.getMessage());
						ws.setStatus("FAILURE");
					}
					websiteRepository.save(ws);
				}

			}
		}

		return "website_check";
	}

	@PostMapping("/websiteCheck")
	public String enterWebsite(@RequestParam String website, Model model, HttpSession session) {

		String newWebsite = "";
		String[] web = website.split("\\.");

		if (web.length != 0) {
			model.addAttribute("websites", websiteRepository.findAll());
			Websites site = new Websites();

			if (web[0].equals("www") || web[0].equals("http://www") || web[0].equals("https://www")) {
				newWebsite = website;
				if (web[0].equals("www")) {
					website = "https://" + website;

				}
				site.setWebsite(newWebsite);

				try {
					URL url = new URL(website);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					int responseCode = connection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {

						site.setStatus("SUCCESS");
					} else {
						site.setStatus("FAILURE");
					}
					connection.disconnect();
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
					site.setStatus("FAILURE");
				}

				websiteRepository.save(site);
				session.setAttribute("message", new Message("URL added Successfully !", "success"));
			} else {
				session.setAttribute("message", new Message("Please Provide Proper url !", "danger"));
				return "redirect:/websiteCheck";
			}
		} else {
			session.setAttribute("message", new Message("Please Provide Proper url !", "danger"));
			return "redirect:/websiteCheck";
		}

		return "redirect:/websiteCheck";
	}

	@GetMapping("/website/clear")
	public String websiteClear(Model model, HttpSession session) {
		websiteRepository.deleteAll();
		session.setAttribute("message", new Message("Cleard Successfully !", "success"));
		return "redirect:/websiteCheck";
	}

}
