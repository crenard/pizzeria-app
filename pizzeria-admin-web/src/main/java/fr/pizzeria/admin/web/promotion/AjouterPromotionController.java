package fr.pizzeria.admin.web.promotion;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.pizzeria.admin.metier.PizzaService;
import fr.pizzeria.admin.metier.PromotionService;
import fr.pizzeria.model.Client;
import fr.pizzeria.model.Pizza;
import fr.pizzeria.model.Promotion;

@WebServlet("/promotions/new")
public class AjouterPromotionController extends HttpServlet {

	private static final String VUE_AJOUTER_PROMOTIONS = "/WEB-INF/views/promotions/ajouterPromotions.jsp";
	private static final String VUE_LISTER_PROMOTIONS = "/promotions/list";

	@Inject
	private PromotionService promotionService;
	@Inject
	private PizzaService pizzaService;

	/* L'input date renvoi un une date sous la forme yyyy-MM-dd */
	final DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("pizzas", pizzaService.findAll());
		this.getServletContext().getRequestDispatcher(VUE_AJOUTER_PROMOTIONS).forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String code = request.getParameter("code");
		LocalDate dateFinPromotion = null;
		try {
			dateFinPromotion = LocalDate.parse(request.getParameter("dateFinPromotion"), dtf1);
		} catch (DateTimeParseException e1) {
			try {
				dateFinPromotion = LocalDate.parse(request.getParameter("dateFinPromotion"), dtf2);
			} catch (DateTimeParseException e2) {
				request.setAttribute("msg", "Le format de la date est inccorect. Veuillez utiliser le format indiqué.");
				this.getServletContext().getRequestDispatcher(VUE_AJOUTER_PROMOTIONS).forward(request, response);
			}
		}
		int reductionEnPourcentage = Integer.parseInt(request.getParameter("reductionEnPourcentage"));
		Pizza pizza = pizzaService.findById(Integer.parseInt(request.getParameter("pizza")));
		promotionService.save(new Promotion(code, dateFinPromotion, reductionEnPourcentage, pizza));
		response.sendRedirect(request.getContextPath() + VUE_LISTER_PROMOTIONS);
	}

}
