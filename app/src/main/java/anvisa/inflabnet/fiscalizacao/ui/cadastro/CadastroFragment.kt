package anvisa.inflabnet.fiscalizacao.ui.cadastro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import anvisa.inflabnet.fiscalizacao.R
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabase
import anvisa.inflabnet.fiscalizacao.database.service.AppDatabaseService
import anvisa.inflabnet.fiscalizacao.login.MainActivity
import anvisa.inflabnet.fiscalizacao.ui.cadastro.viewmodel.CadastroViewModel
import com.facebook.login.LoginManager
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_cadastro.*


class CadastroFragment : Fragment() {

    private lateinit var cadastroViewModel: CadastroViewModel
    private lateinit var appDatabase : AppDatabase
    private var mAuth: FirebaseAuth? = null

    //GPS
    private val PERMISSION_ID = 30
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cadastroViewModel =
            ViewModelProviders.of(requireActivity()).get(CadastroViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cadastro, container, false)

        val contextFrag = requireActivity().applicationContext
        appDatabase = AppDatabaseService.getInstance(contextFrag)

        //pegar localização atual
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastLocation()

        return root
    }

    override fun onResume() {
        super.onResume()

        //verificar se há valores
        val sharedPrefsChech: SharedPreferences = requireContext().getSharedPreferences("Armazenamento_Temporario", Context.MODE_PRIVATE)
        val municipioShP = sharedPrefsChech.getString("municipio","_Município$")
        val bairroShP = sharedPrefsChech.getString("bairro","_Bairro$")
        val estabelecimentoShP = sharedPrefsChech.getString("estabelecimento","_Estabelecimento$")

        if(!municipioShP.isNullOrBlank()){
            acMunicipios.setText(municipioShP)
        }
        if(municipioShP.equals("_Município$")){
            acMunicipios.setText("")
        }
        if(!bairroShP.isNullOrBlank()){
            acBairros.setText(bairroShP)
        }
        if (bairroShP.equals("_Bairro$")) {
            acBairros.setText("")
        }
        if(!estabelecimentoShP.isNullOrBlank()){
            edtEstabelecimento.setText(estabelecimentoShP)
        }
        if(estabelecimentoShP.equals("_Estabelecimento$")){
            edtEstabelecimento.setText("")
        }

        //atualiza localização
        getLastLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //deu trabalho, mas duas APIs para consulta de municipios do RJ e bairros do Rio
        cadastroViewModel.listaMunicipiosAutoComplete(requireContext(), acMunicipios)
        cadastroViewModel.listaBairrosAutoComplete(requireContext(),acBairros)

        btnAutuacao.setOnClickListener {


            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(btnProximo.windowToken, 0)

            if(acMunicipios.text.toString().isBlank() || acBairros.text.toString().isBlank() || edtEstabelecimento.text.toString().isBlank()){
                showSnackbar("Favro preencher todos os campos!")
            } else {
                //viewmodel só para dizer que tentei
                cadastroViewModel.setMunicipio(edtEstabelecimento.text.toString().trimEnd())

                //shared preferences funciona
                val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences(
                    "Armazenamento_Temporario",
                    Context.MODE_PRIVATE
                )
                val editor = sharedPrefs.edit()
                editor.putString("municipio", acMunicipios.text.toString().trimEnd())
                editor.putString("bairro", acBairros.text.toString().trimEnd())
                editor.putString("estabelecimento", edtEstabelecimento.text.toString().trimEnd())
                editor.putString("lat", txtLatitude.text.toString())
                editor.putString("lon", txtLongitude.text.toString())
                editor.apply()

                findNavController().navigate(R.id.action_navigation_cadastro_to_cadAutuacaoFragment)
            }

        }

        btnProximo.setOnClickListener {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(btnProximo.windowToken, 0)

            if(acMunicipios.text.toString().isBlank() || acBairros.text.toString().isBlank() || edtEstabelecimento.text.toString().isBlank()){
                showSnackbar("Favro preencher todos os campos!")
            } else{
                //viewmodel só para dizer que tentei
                cadastroViewModel.setMunicipio(edtEstabelecimento.text.toString().trimEnd())

                //shared preferences funciona
                val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences("Armazenamento_Temporario", Context.MODE_PRIVATE)
                val editor = sharedPrefs.edit()
                editor.putString("municipio", acMunicipios.text.toString().trimEnd())
                editor.putString("bairro", acBairros.text.toString().trimEnd())
                editor.putString("estabelecimento", edtEstabelecimento.text.toString().trimEnd())
                editor.apply()


                findNavController().navigate(R.id.action_navigation_cadastro_to_perguntasFragment )
            }
        }

        //botão voltar
        logoutBtn.setOnClickListener {
            LoginManager.getInstance().logOut()
            mAuth = FirebaseAuth.getInstance()
            mAuth!!.signOut()

            val novoIntt = Intent(requireContext(), MainActivity::class.java)
            novoIntt.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(novoIntt)

        }
    }

    private fun showSnackbar(msg: String?) {
        val snack = Snackbar.make(novoEstabRoot,msg.toString(), Snackbar.LENGTH_LONG)
        snack.show()
    }

    //pegar localização
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        txtLatitude.text = location.latitude.toString()
                        txtLongitude.text = location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Localização desligada. O GPS deve estar ligado para seguir", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        //mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            txtLatitude.text = mLastLocation.latitude.toString()
            txtLongitude.text = mLastLocation.longitude.toString()
        }
    }
}
