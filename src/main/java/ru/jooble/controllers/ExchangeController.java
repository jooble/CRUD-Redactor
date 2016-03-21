package ru.jooble.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;
import ru.jooble.controllers.forms.ExchangeForm;
import ru.jooble.controllers.validator.ExchangeFromValidator;
import ru.jooble.domain.Currency;
import ru.jooble.domain.Exchange;
import ru.jooble.service.CurrencyService;
import ru.jooble.service.ExchangeService;

import java.util.List;
import java.util.Locale;

@Controller
public class ExchangeController {
    public static final String ERROR_PAGE = "errorPage";
    public static final String SAVE_EXCHANGE = "saveExchange";

    @Autowired
    ExchangeService exchangeService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    ExchangeFromValidator exchangeFromValidator;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(exchangeFromValidator);
    }


    @RequestMapping(value = "/all/exchange", method = RequestMethod.GET)
    public String showAllUsers(ModelMap model) {
        model.addAttribute("exchanges", exchangeService.getAll());
        return "allExchange";
    }

    @RequestMapping(value = "/save/exchange", method = RequestMethod.GET)
    public String showPageAddExchange(ModelMap model) {
        List<Currency> currencies = currencyService.getAll();
        model.addAttribute("exchangeForm", new ExchangeForm());
        model.addAttribute("sourceCurrencies", currencies);
        model.addAttribute("targetCurrencies", currencies);
        return SAVE_EXCHANGE;
    }

    @RequestMapping(value = "/save/exchange/{id}", method = RequestMethod.GET)
    public String showPageEditExchange(@PathVariable(value = "id") Long id, ModelMap model) {
        Exchange exchange = exchangeService.getById(id);
        List<Currency> currencies = currencyService.getAll();
        if (exchange == null) {
            return ERROR_PAGE;
        }
        model.addAttribute("exchangeForm", new ExchangeForm(exchange));
        model.addAttribute("sourceCurrencies", currencies);
        model.addAttribute("targetCurrencies", currencies);

        return SAVE_EXCHANGE;
    }

    @RequestMapping(value = "/save/exchange", method = RequestMethod.POST)
    public String saveExchange(@Validated ExchangeForm exchangeForm, BindingResult bindingResult, ModelMap model) {
        if (bindingResult.hasErrors()) {
            List<Currency> currencies = currencyService.getAll();
            model.addAttribute("sourceCurrencies", currencies);
            model.addAttribute("targetCurrencies", currencies);
            return SAVE_EXCHANGE;
        }
        if ("".equals(exchangeForm.getId())) {
            exchangeService.insert(new Exchange(0, Long.parseLong(exchangeForm.getSourceCurrencyId()),
                    Long.parseLong(exchangeForm.getTargetCurrencyId()), Double.parseDouble(exchangeForm.getExchangeRate())));
        } else {
            exchangeService.update(new Exchange(Long.parseLong(exchangeForm.getId()), Long.parseLong(exchangeForm.getSourceCurrencyId()),
                    Long.parseLong(exchangeForm.getTargetCurrencyId()), Double.parseDouble(exchangeForm.getExchangeRate())));
        }
        return "redirect:/all/exchange";
    }

    @RequestMapping(value = "/delete/exchange/{id}", method = RequestMethod.GET)
    public RedirectView deleteExchange(@PathVariable(value = "id") Long id){
        exchangeService.deleteById(id);
        return new RedirectView("/all/exchange");
    }
}