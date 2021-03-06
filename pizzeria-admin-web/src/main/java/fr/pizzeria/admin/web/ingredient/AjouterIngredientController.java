package fr.pizzeria.admin.web.ingredient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.pizzeria.admin.exception.StockageException;
import fr.pizzeria.admin.metier.IngredientService;
import fr.pizzeria.model.Ingredient;

/**
 * Servlet de la page ajout d'un ingredient.
 */
@WebServlet("/ingredients/ajouter")
public class AjouterIngredientController extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(AjouterIngredientController.class.getName());

	private static final String VUE_LISTER_INGREDIENTS = "/WEB-INF/views/ingredients/ajouterIngredient.jsp";
	private static final String URL_LISTE = "/ingredients/liste";

	@EJB
	private IngredientService ingredientService;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(VUE_LISTER_INGREDIENTS);
		dispatcher.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if (!req.getParameter("nom").isEmpty() && !req.getParameter("quantite").isEmpty()
						&& !req.getParameter("prix").isEmpty()) {

			LOG.log(Level.INFO, "-------!!!-------  nom :" + req.getParameter("nom"));
			LOG.log(Level.INFO, "-------!!!-------  quantite :" + req.getParameter("quantite"));
			LOG.log(Level.INFO, "-------!!!-------  prix :" + req.getParameter("prix"));

			try {

				Ingredient i = new Ingredient(req.getParameter("nom").toString(),
								Integer.valueOf(req.getParameter("quantite").toString()),
								Double.valueOf(req.getParameter("prix").toString()), false);

				LOG.log(Level.INFO, "-------!!!------- Ajout ingrédient :" + i.toString());
				this.ingredientService.save(i);

			} catch (StockageException e) {
				LOG.log(Level.WARNING, "-------!!!------- exception levée : " + e.getMessage() + " => " + e.getCause());
				req.setAttribute("msg", "Erreur du serveur, merci de contacter le support de l'application ");
			} finally {
				resp.sendRedirect(req.getContextPath() + URL_LISTE);
			}

		} else {
			String erreur[] = { "", "", "" };
			if (req.getParameter("nom").isEmpty()) {
				erreur[0] = "red";
			} else {
				req.setAttribute("nom", req.getParameter("nom"));
			}

			if (req.getParameter("quantite").isEmpty()) {
				erreur[1] = "red";
			} else {
				req.setAttribute("quantite", req.getParameter("quantite"));
			}

			if (req.getParameter("prix").isEmpty()) {
				erreur[2] = "red";
			} else {
				req.setAttribute("prix", req.getParameter("prix"));
			}

			req.setAttribute("erreur", erreur);
			req.setAttribute("msg", "Veuillez saisir les champs en rouge : ");
			this.doGet(req, resp);
		}
	}
}
