'use client';

import { useEffect, useMemo, useState } from 'react';

const API =
  process.env.NEXT_PUBLIC_API_BASE || 'http://localhost:8080';

type AnyMap = Record<string, any>;

function api(
  path: string,
  token: string,
  init?: RequestInit
) {
  return fetch(API + path, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
      ...(init?.headers || {}),
    },
  }).then(async (r) => {
    const text = await r.text();

    let j: AnyMap = {};

    try {
      j = text ? JSON.parse(text) : {};
    } catch {
      j = {
        message: text,
      };
    }

    console.log('API REQUEST:', API + path);
    console.log('API STATUS:', r.status);
    console.log('API RESPONSE:', j);

    if (!r.ok) {
      throw new Error(
        j.message ||
          j.error ||
          `HTTP ${r.status}`
      );
    }

    return j.data ?? j;
  });
}

/* =========================================================
   BAR COMPONENT
   ========================================================= */

function Bars({ items }: { items: any[] }) {
  const max = Math.max(
    1,
    ...items.map((x) =>
      Number(
        x.count ??
          x.value ??
          x.applications ??
          0
      )
    )
  );

  return (
    <div className="bars">
      {items.slice(0, 10).map((x, i) => {
        const value = Number(
          x.count ??
            x.value ??
            x.applications ??
            0
        );

        return (
          <div className="barrow" key={i}>
            <span>
              {x.name ??
                x.group ??
                x.source}
            </span>

            <div className="track">
              <div
                className="fill"
                style={{
                  width: `${Math.min(
                    100,
                    (value / max) * 100
                  )}%`,
                }}
              />
            </div>

            <b>{value}</b>
          </div>
        );
      })}
    </div>
  );
}

/* =========================================================
   ROLE OPTIONS
   ========================================================= */

const ROLE_OPTIONS = [
  {
    label: 'CEO',
    value: 'CEO',
    description: 'Executive workforce overview',
  },
  {
    label: 'CHRO',
    value: 'CHRO',
    description: 'Chief HR analytics',
  },
  {
    label: 'HR',
    value: 'HR',
    description: 'HR and people analytics',
  },
  {
    label: 'Finance',
    value: 'FINANCE',
    description: 'Payroll and workforce cost',
  },
  {
    label: 'HR Business Partner',
    value: 'HR_BP',
    description: 'Business-unit workforce analytics',
  },
  {
    label: 'Manager',
    value: 'MANAGER',
    description: 'Team workforce analytics',
  },
  {
    label: 'Employee',
    value: 'EMPLOYEE',
    description: 'Personal workforce information',
  },
];

/* =========================================================
   ROLE -> VISIBLE TABS
   IMPORTANT:
   Keep this declaration ONLY ONCE in this file.
   ========================================================= */

const ROLE_TABS: Record<string, string[]> = {
  CEO: [
    'dashboard',
    'attendance',
    'recruitment',
    'forecast',
    'pay-equity',
    'reports',
  ],

  CHRO: [
    'dashboard',
    'attendance',
    'recruitment',
    'forecast',
    'pay-equity',
    'reports',
  ],

  HR: [
    'dashboard',
    'attendance',
    'recruitment',
    'reports',
  ],

  FINANCE: [
    'dashboard',
    'reports',
  ],

  HR_BP: [
    'dashboard',
    'attendance',
    'recruitment',
  ],

  MANAGER: [
    'dashboard',
    'attendance',
  ],

  EMPLOYEE: [
    'dashboard',
  ],
};

/* =========================================================
   TAB LABELS
   ========================================================= */

const TAB_LABELS: Record<string, string> = {
  dashboard: 'Dashboard',
  attendance: 'Attendance',
  recruitment: 'Recruitment',
  forecast: 'Forecast',
  'pay-equity': 'Pay Equity',
  reports: 'Reports',
};

/* =========================================================
   MAIN PAGE
   ========================================================= */

export default function Page() {
  const [token, setToken] = useState('');

  const [email, setEmail] = useState('');

  const [password, setPassword] = useState('');

  const [dashboard, setDashboard] = useState('EMPLOYEE');

  const [data, setData] =
    useState<AnyMap | null>(null);

  const [tab, setTab] =
    useState('dashboard');

  const [question, setQuestion] =
    useState('');

  const [answer, setAnswer] =
    useState<AnyMap | null>(null);

  const [error, setError] =
    useState('');

  const [userRole, setUserRole] =
    useState('');

  const [loading, setLoading] =
    useState(false);

  const [loginLoading, setLoginLoading] =
    useState(false);

  const [showPassword, setShowPassword] =
    useState(false);

  const [rememberMe, setRememberMe] =
    useState(false);

  const [registerMode, setRegisterMode] =
    useState(false);

  const [confirmPassword, setConfirmPassword] =
    useState('');

  const [from, setFrom] = useState(() => {
    const d = new Date();

    d.setMonth(d.getMonth() - 11);

    return d.toISOString().slice(0, 10);
  });

  const [to, setTo] = useState(() =>
    new Date()
      .toISOString()
      .slice(0, 10)
  );

  /* =========================================================
     LOGIN
     ========================================================= */

  const login = async () => {
    setError('');

    if (!email.trim()) {
      setError('Please enter your email.');
      return;
    }

    if (!password) {
      setError('Please enter your password.');
      return;
    }

    setLoading(true);

    try {
      const r = await fetch(
        `${API}/api/auth/login`,
        {
          method: 'POST',

          headers: {
            'Content-Type': 'application/json',
          },

          body: JSON.stringify({
            email: email.trim(),
            password,
          }),
        }
      );

      const j =
        await r.json().catch(() => ({}));

      console.log(
        'LOGIN RESPONSE:',
        j
      );

      console.log(
        'LOGIN ROLE:',
        j.role
      );

      if (!r.ok) {
        throw new Error(
          j.message ||
            j.error ||
            'Login failed'
        );
      }

      if (!j.token) {
        throw new Error(
          'Login succeeded but no token was returned.'
        );
      }

      /* Get role only once */
      const role =
        j.role || 'EMPLOYEE';

      const supportedRoles = [
        'CEO',
        'CHRO',
        'HR',
        'FINANCE',
        'HR_BP',
        'MANAGER',
        'EMPLOYEE',
      ];

      /*
       * Store role in React state
       */
      setUserRole(role);

      /*
       * Set dashboard based on role
       */
      if (
        supportedRoles.includes(role)
      ) {
        setDashboard(role);
      } else {
        setDashboard('EMPLOYEE');
      }

      /*
       * Always start at Dashboard after login
       */
      setTab('dashboard');

      /*
       * Store token and role
       */
      if (rememberMe) {
        localStorage.setItem(
          'lens_token',
          j.token
        );

        localStorage.setItem(
          'lens_role',
          role
        );

        sessionStorage.removeItem(
          'lens_token'
        );

        sessionStorage.removeItem(
          'lens_role'
        );
      } else {
        sessionStorage.setItem(
          'lens_token',
          j.token
        );

        sessionStorage.setItem(
          'lens_role',
          role
        );

        localStorage.removeItem(
          'lens_token'
        );

        localStorage.removeItem(
          'lens_role'
        );
      }

      /*
       * Update React state
       */
      setToken(j.token);
    } catch (e: any) {
      setError(
        e.message ||
          'Unable to sign in.'
      );
    } finally {
      setLoading(false);
    }
  };

  /* =========================================================
     RESTORE LOGIN SESSION
     ========================================================= */

  useEffect(() => {
    const rememberedToken =
      localStorage.getItem(
        'lens_token'
      );

    const sessionToken =
      sessionStorage.getItem(
        'lens_token'
      );

    const existingToken =
      rememberedToken ||
      sessionToken;

    const rememberedRole =
      localStorage.getItem(
        'lens_role'
      );

    const sessionRole =
      sessionStorage.getItem(
        'lens_role'
      );

    const existingRole =
      rememberedRole ||
      sessionRole;

    if (
      existingToken &&
      existingRole
    ) {
      setToken(existingToken);

      setUserRole(existingRole);

      setDashboard(existingRole);

      setTab('dashboard');
    }
  }, []);

  /* =========================================================
     LOAD DASHBOARD DATA
     ========================================================= */

  const load = async () => {
    if (!token) {
      return;
    }

    setLoading(true);

    setError('');

    try {
      const result =
        await api(
          `/api/analytics/dashboards/${dashboard}/data?from=${from}&to=${to}`,
          token
        );

      setData(result);
    } catch (e: any) {
      setError(
        e.message ||
          'Failed to load dashboard.'
      );
    } finally {
      setLoading(false);
    }
  };

  /* =========================================================
     LOAD WHEN TOKEN / ROLE / DATE CHANGES
     ========================================================= */

  useEffect(() => {
    if (token) {
      load();
    }
  }, [
    token,
    dashboard,
    from,
    to,
  ]);

  /* =========================================================
     REGISTER
     ========================================================= */

  const register = async () => {
    setError('');

    if (!email.trim()) {
      setError(
        'Please enter your email.'
      );

      return;
    }

    if (!password) {
      setError(
        'Please enter your password.'
      );

      return;
    }

    if (
      password !==
      confirmPassword
    ) {
      setError(
        'Passwords do not match.'
      );

      return;
    }

    setLoginLoading(true);

    try {
      const r = await fetch(
        `${API}/api/auth/register`,
        {
          method: 'POST',

          headers: {
            'Content-Type':
              'application/json',
          },

          body: JSON.stringify({
            email: email.trim(),
            password,
          }),
        }
      );

      const j =
        await r.json().catch(() => ({}));

      if (!r.ok) {
        throw new Error(
          j.message ||
            j.error ||
            'Registration failed'
        );
      }

      setRegisterMode(false);

      setPassword('');

      setConfirmPassword('');

      setError(
        'Registration successful. Please login.'
      );
    } catch (e: any) {
      setError(
        e.message ||
          'Unable to register.'
      );
    } finally {
      setLoginLoading(false);
    }
  };

  /* =========================================================
     ASK LENS
     ========================================================= */

  const ask = async () => {
    if (!question.trim()) {
      return;
    }

    try {
      setError('');

      const result =
        await api(
          '/api/analytics/ask',
          token,
          {
            method: 'POST',

            body: JSON.stringify({
              question,
            }),
          }
        );

      setAnswer(result);
    } catch (e: any) {
      setError(
        e.message ||
          'Unable to process your question.'
      );
    }
  };

  /* =========================================================
     KPI CARDS
     ========================================================= */

  const cards = useMemo(() => {
    if (!data) {
      return [];
    }

    switch (userRole) {
      case 'CEO':
        return [
          [
            'Total Headcount',
            data.headcount?.total ?? 0,
          ],

          [
            'Attrition',
            `${data.attrition?.overall_rate ?? 0}%`,
          ],

          [
            'Payroll Cost',
            data.payrollCost?.total_cost ?? 0,
          ],

          [
            'Attendance',
            `${data.attendance?.average_attendance_rate ?? 0}%`,
          ],

          [
            'Applications',
            data.recruitment?.applications ?? 0,
          ],

          [
            'New Joiners MTD',
            data.headcount?.new_joiners_mtd ?? 0,
          ],
        ];

      case 'CHRO':
        return [
          [
            'Total Headcount',
            data.headcount?.total ?? 0,
          ],

          [
            'Attrition',
            `${data.attrition?.overall_rate ?? 0}%`,
          ],

          [
            'Attendance',
            `${data.attendance?.average_attendance_rate ?? 0}%`,
          ],

          [
            'Applications',
            data.recruitment?.applications ?? 0,
          ],

          [
            'New Joiners',
            data.headcount?.new_joiners_mtd ?? 0,
          ],

          [
            'Separations',
            data.headcount?.separations_mtd ?? 0,
          ],
        ];

      case 'HR':
        return [
          [
            'Headcount',
            data.headcount?.total ?? 0,
          ],

          [
            'Joiners MTD',
            data.headcount?.new_joiners_mtd ?? 0,
          ],

          [
            'Separations MTD',
            data.headcount?.separations_mtd ?? 0,
          ],

          [
            'Attrition',
            `${data.attrition?.overall_rate ?? 0}%`,
          ],

          [
            'Attendance',
            `${data.attendance?.average_attendance_rate ?? 0}%`,
          ],

          [
            'Applications',
            data.recruitment?.applications ?? 0,
          ],
        ];

      case 'FINANCE':
        return [
          [
            'Payroll Cost',
            data.payrollCost?.total_cost ?? 0,
          ],

          [
            'Cost / Employee',
            data.payrollCost?.cost_per_employee ?? 0,
          ],

          [
            'Budget',
            data.payrollCost?.budget ?? 0,
          ],

          [
            'Variance',
            data.payrollCost?.variance ?? 0,
          ],

          [
            'Headcount',
            data.headcount?.total ?? 0,
          ],
        ];

      case 'HR_BP':
        return [
          [
            'Headcount',
            data.headcount?.total ?? 0,
          ],

          [
            'Attrition',
            `${data.attrition?.overall_rate ?? 0}%`,
          ],

          [
            'Attendance',
            `${data.attendance?.average_attendance_rate ?? 0}%`,
          ],

          [
            'Recruitment',
            data.recruitment?.applications ?? 0,
          ],

          [
            'Joiners MTD',
            data.headcount?.new_joiners_mtd ?? 0,
          ],
        ];

      case 'MANAGER':
        return [
          [
            'Team Headcount',
            data.headcount?.total ?? 0,
          ],

          [
            'Attendance',
            `${data.attendance?.average_attendance_rate ?? 0}%`,
          ],

          [
            'Absenteeism',
            `${data.attendance?.absenteeism_rate ?? 0}%`,
          ],

          [
            'Attrition',
            `${data.attrition?.overall_rate ?? 0}%`,
          ],
        ];

      case 'EMPLOYEE':
        return [
          [
            'My Information',
            'SELF',
          ],
        ];

      default:
        return [];
    }
  }, [
    data,
    userRole,
  ]);

  /* =========================================================
     VISIBLE TABS
     IMPORTANT:
     This is the ONLY visibleTabs declaration.
     ========================================================= */

  const visibleTabs =
    ROLE_TABS[userRole] ||
    ['dashboard'];

  /* =========================================================
     LOGIN SCREEN
     ========================================================= */

  if (!token) {
    return (
      <main className="login-page">
        <div className="login-glow login-glow-one" />

        <div className="login-glow login-glow-two" />

        <div className="login-card">
          <h1 className="login-title">
            LENS
          </h1>

          <div className="login-form">

            {/* Email */}

            <div className="login-field-group">
              <label className="login-label">
                Email
              </label>

              <div className="login-input-wrapper">
                <span className="login-icon">
                  <svg
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <rect
                      x="3"
                      y="5"
                      width="18"
                      height="14"
                      rx="2"
                      stroke="currentColor"
                      strokeWidth="1.8"
                    />

                    <path
                      d="M4 7L12 13L20 7"
                      stroke="currentColor"
                      strokeWidth="1.8"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                    />
                  </svg>
                </span>

                <input
                  type="email"
                  value={email}
                  onChange={(e) =>
                    setEmail(
                      e.target.value
                    )
                  }
                  placeholder="Enter your email"
                  className="login-input"
                  autoComplete="email"
                />
              </div>
            </div>

            {/* Password */}

            <div className="login-field-group">
              <label className="login-label">
                Password
              </label>

              <div className="login-input-wrapper">
                <span className="login-icon">
                  <svg
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <rect
                      x="5"
                      y="10"
                      width="14"
                      height="10"
                      rx="2"
                      stroke="currentColor"
                      strokeWidth="1.8"
                    />

                    <path
                      d="M8 10V7.5C8 5.29 9.79 3.5 12 3.5C14.21 3.5 16 5.29 16 7.5V10"
                      stroke="currentColor"
                      strokeWidth="1.8"
                      strokeLinecap="round"
                    />

                    <circle
                      cx="12"
                      cy="15"
                      r="1"
                      fill="currentColor"
                    />
                  </svg>
                </span>

                <input
                  type={
                    showPassword
                      ? 'text'
                      : 'password'
                  }
                  value={password}
                  onChange={(e) =>
                    setPassword(
                      e.target.value
                    )
                  }
                  placeholder="Enter your password"
                  className="login-input"
                  autoComplete={
                    registerMode
                      ? 'new-password'
                      : 'current-password'
                  }
                />

                <button
                  type="button"
                  className="password-toggle"
                  onClick={() =>
                    setShowPassword(
                      !showPassword
                    )
                  }
                  aria-label={
                    showPassword
                      ? 'Hide password'
                      : 'Show password'
                  }
                >
                  {showPassword ? (
                    <svg
                      width="25"
                      height="25"
                      viewBox="0 0 24 24"
                      fill="none"
                    >
                      <path
                        d="M2.5 12C4.5 8.5 7.7 6.5 12 6.5C16.3 6.5 19.5 8.5 21.5 12C19.5 15.5 16.3 17.5 12 17.5C7.7 17.5 4.5 15.5 2.5 12Z"
                        stroke="currentColor"
                        strokeWidth="1.8"
                      />

                      <circle
                        cx="12"
                        cy="12"
                        r="3"
                        stroke="currentColor"
                        strokeWidth="1.8"
                      />
                    </svg>
                  ) : (
                    <svg
                      width="25"
                      height="25"
                      viewBox="0 0 24 24"
                      fill="none"
                    >
                      <path
                        d="M3 3L21 21"
                        stroke="currentColor"
                        strokeWidth="1.8"
                        strokeLinecap="round"
                      />

                      <path
                        d="M10.6 10.6C10.2 11 10 11.5 10 12C10 13.1 10.9 14 12 14C12.5 14 13 13.8 13.4 13.4"
                        stroke="currentColor"
                        strokeWidth="1.8"
                        strokeLinecap="round"
                      />

                      <path
                        d="M9.9 5.1C10.6 4.9 11.3 4.8 12 4.8C16.4 4.8 19.6 7.4 21.5 12C20.7 13.8 19.6 15.2 18.3 16.3"
                        stroke="currentColor"
                        strokeWidth="1.8"
                        strokeLinecap="round"
                      />

                      <path
                        d="M6.1 6.1C4.6 7.4 3.4 9.3 2.5 12C4.4 16.6 7.6 19.2 12 19.2C13.4 19.2 14.7 18.9 15.8 18.4"
                        stroke="currentColor"
                        strokeWidth="1.8"
                        strokeLinecap="round"
                      />
                    </svg>
                  )}
                </button>
              </div>

              <div className="forgot-row">
                <button
                  type="button"
                  className="forgot-password"
                  onClick={() =>
                    setError(
                      'Password reset is not configured yet.'
                    )
                  }
                >
                  Forgot Password?
                </button>
              </div>
            </div>

            {/* Confirm Password */}

            {registerMode && (
              <div className="login-field-group">
                <label className="login-label">
                  Confirm Password
                </label>

                <div className="login-input-wrapper">
                  <input
                    type="password"
                    value={
                      confirmPassword
                    }
                    onChange={(e) =>
                      setConfirmPassword(
                        e.target.value
                      )
                    }
                    placeholder="Confirm your password"
                    className="login-input"
                    autoComplete="new-password"
                  />
                </div>
              </div>
            )}

            {/* Remember Me */}

            <label className="remember-row">
              <input
                type="checkbox"
                checked={
                  rememberMe
                }
                onChange={(e) =>
                  setRememberMe(
                    e.target.checked
                  )
                }
              />

              <span>
                Remember Me
              </span>
            </label>

            {/* Error */}

            {error && (
              <div className="login-error">
                {error}
              </div>
            )}

            {/* Login / Register */}

            <button
              type="button"
              className="login-button"
              onClick={
                registerMode
                  ? register
                  : login
              }
              disabled={
                loading ||
                loginLoading
              }
            >
              {registerMode
                ? loginLoading
                  ? 'Registering...'
                  : 'Register'
                : loading
                  ? 'Logging in...'
                  : 'Login'}
            </button>

            {/* Register / Login toggle */}

            <div className="register-row">
              {registerMode ? (
                <>
                  <span>
                    Already have an Account?
                  </span>

                  <button
                    type="button"
                    className="register-button"
                    onClick={() => {
                      setRegisterMode(
                        false
                      );

                      setError('');

                      setPassword('');

                      setConfirmPassword(
                        ''
                      );
                    }}
                  >
                    Login
                  </button>
                </>
              ) : (
                <>
                  <span>
                    Don't have an Account?
                  </span>

                  <button
                    type="button"
                    className="register-button"
                    onClick={() => {
                      setRegisterMode(
                        true
                      );

                      setError('');

                      setPassword('');

                      setConfirmPassword(
                        ''
                      );
                    }}
                  >
                    Register
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      </main>
    );
  }

  /* =========================================================
     DASHBOARD
     ========================================================= */

  return (
    <main className="shell">

      {/* =====================================================
          TOP BAR
          ===================================================== */}

      <div className="top">
        <div className="brand">
          <h1>
            Lens · Insights
          </h1>

          <p>
            Workforce analytics, reports and AI
          </p>
        </div>

        <div className="toolbar">

          <select value={dashboard}
          onChange={(e) =>
            { const selectedRole = e.target.value;

                setDashboard(selectedRole);
                setUserRole(selectedRole);

                // Clear old dashboard data while the new
                // // role's dashboard is loading.
                setData(null);

                // Reset to dashboard tab whenever
                // another role is selected.
                setTab('dashboard');

                // Clear previous Ask Lens answer.
                setAnswer(null);

                // Clear previous error.
                setError('');
              }}
            >
             {ROLE_OPTIONS.map((role) => (
                <option
                  key={`${role.label}-${role.value}`}
                  value={role.value}
                >
                  {role.label}
                </option>
            ))}
        </select>

          <input
            type="date"
            value={from}
            onChange={(e) =>
              setFrom(
                e.target.value
              )
            }
          />

          <input
            type="date"
            value={to}
            onChange={(e) =>
              setTo(
                e.target.value
              )
            }
          />

          <button
            onClick={load}
          >
            Refresh
          </button>

          <button
            onClick={() => {
              localStorage.removeItem(
                'lens_token'
              );

              localStorage.removeItem(
                'lens_role'
              );

              sessionStorage.removeItem(
                'lens_token'
              );

              sessionStorage.removeItem(
                'lens_role'
              );

              setToken('');

              setUserRole('');

              setDashboard(
                'EMPLOYEE'
              );

              setTab(
                'dashboard'
              );

              setData(null);

              setAnswer(null);

              setError('');

              setQuestion('');
            }}
          >
            Logout
          </button>
        </div>
      </div>

      {/* =====================================================
          ROLE DESCRIPTION
          ===================================================== */}

      <div className="role-description">
        <strong>
          {
            ROLE_OPTIONS.find(
              (x) =>
                x.value ===
                dashboard
            )?.label
          }
        </strong>

        <span>
          {
            ROLE_OPTIONS.find(
              (x) =>
                x.value ===
                dashboard
            )?.description
          }
        </span>
      </div>

      {/* =====================================================
          NAVIGATION TABS
          ===================================================== */}

      <div className="tabs">
        {visibleTabs.map((item) => (
            <button
              key={item}
              className={tab === item ? 'tab active' : 'tab'}
              onClick={() => setTab(item)}
            >
              {item}
            </button>
        ))}
    </div>

      {/* =====================================================
          ERROR
          ===================================================== */}

      {error && (
        <p className="error">
          {error}
        </p>
      )}

      {/* =====================================================
          LOADING
          ===================================================== */}

      {loading && (
        <p className="muted">
          Loading analytics…
        </p>
      )}

      {/* =====================================================
          DASHBOARD TAB
          ===================================================== */}

      {tab === 'dashboard' &&
        data && (
          <>
            <div className="grid">
              {cards.map(
                ([k, v]) => (
                  <div
                    className="card"
                    key={String(k)}
                  >
                    <div className="muted">
                      {k}
                    </div>

                    <div className="metric">
                      {v}
                    </div>
                  </div>
                )
              )}
            </div>

            <div className="two section">

              {/* Headcount */}

              <div className="card">
                <h2>
                  Headcount by department
                </h2>

                <Bars
                  items={
                    data.headcount
                      ?.by_department ||
                    []
                  }
                />
              </div>

              {/* Attrition */}

              <div className="card">
                <h2>
                  Attrition trend
                </h2>

                <table className="table">
                  <thead>
                    <tr>
                      <th>
                        Month
                      </th>

                      <th>
                        Separations
                      </th>

                      <th>
                        Rate
                      </th>
                    </tr>
                  </thead>

                  <tbody>
                    {(
                      data.attrition
                        ?.trend_12_months ||
                      []
                    ).map(
                      (
                        x: any
                      ) => (
                        <tr
                          key={
                            x.month
                          }
                        >
                          <td>
                            {
                              x.month
                            }
                          </td>

                          <td>
                            {
                              x.separations
                            }
                          </td>

                          <td>
                            {
                              x.attrition_rate
                            }
                            %
                          </td>
                        </tr>
                      )
                    )}
                  </tbody>
                </table>
              </div>
            </div>

            {/* Payroll */}

            {data.payrollCost && (
              <div className="two section">

                <div className="card">
                  <h2>
                    Payroll cost
                  </h2>

                  <div className="metric">
                    {
                      data
                        .payrollCost
                        ?.total_cost
                    }
                  </div>

                  <p className="muted">
                    Total payroll cost
                  </p>
                </div>

                <div className="card">
                  <h2>
                    Cost by department
                  </h2>

                  <pre className="data-box">
                    {JSON.stringify(
                      data
                        .payrollCost
                        ?.by_department ||
                        {},
                      null,
                      2
                    )}
                  </pre>
                </div>

              </div>
            )}
          </>
        )}

      {/* =====================================================
          ATTENDANCE TAB
          ===================================================== */}

      {tab === 'attendance' &&
        data && (
          <div className="grid">

            <div className="card">
              <span className="muted">
                Attendance
              </span>

              <div className="metric">
                {
                  data.attendance
                    ?.average_attendance_rate
                }
                %
              </div>
            </div>

            <div className="card">
              <span className="muted">
                Late arrival
              </span>

              <div className="metric">
                {
                  data.attendance
                    ?.late_arrival_rate
                }
                %
              </div>
            </div>

            <div className="card">
              <span className="muted">
                Absenteeism
              </span>

              <div className="metric">
                {
                  data.attendance
                    ?.absenteeism_rate
                }
                %
              </div>
            </div>

            <div className="card">
              <span className="muted">
                Overtime months
              </span>

              <div className="metric">
                {
                  data.attendance
                    ?.overtime_trend
                    ?.length ||
                  0
                }
              </div>
            </div>

          </div>
        )}

      {/* =====================================================
          RECRUITMENT TAB
          ===================================================== */}

      {tab === 'recruitment' &&
        data && (
          <div className="two">

            <div className="card">
              <h2>
                Recruitment funnel
              </h2>

              <Bars
                items={[
                  'applications',
                  'screened',
                  'interviewed',
                  'offered',
                  'hired',
                ].map(
                  (k) => ({
                    name: k,

                    value:
                      data
                        .recruitment?.[
                        k
                      ] || 0,
                  })
                )}
              />

              <p>
                Offer acceptance:{' '}
                {
                  data
                    .recruitment
                    ?.offer_acceptance_rate
                }
                %
              </p>

              <p>
                Time to hire p50/p90:{' '}
                {
                  data
                    .recruitment
                    ?.time_to_hire
                    ?.p50
                }
                /
                {
                  data
                    .recruitment
                    ?.time_to_hire
                    ?.p90
                }{' '}
                days
              </p>
            </div>

            <div className="card">
              <h2>
                Source mix
              </h2>

              <Bars
                items={
                  data
                    .recruitment
                    ?.source_mix ||
                  []
                }
              />
            </div>

          </div>
        )}

      {/* =====================================================
          FORECAST
          ===================================================== */}

      {tab === 'forecast' && (
        <Forecast
          token={token}
        />
      )}

      {/* =====================================================
          PAY EQUITY
          ===================================================== */}

      {tab === 'pay-equity' && (
        <Equity
          token={token}
        />
      )}

      {/* =====================================================
          REPORTS
          ===================================================== */}

      {tab === 'reports' && (
        <Reports
          token={token}
        />
      )}

      {/* =====================================================
          ASK LENS
          ===================================================== */}

      <div className="card section">
        <h2>
          Ask Lens
        </h2>

        <p className="muted">
          Ask questions about headcount,
          attrition, payroll, attendance or
          recruitment.
        </p>

        <div className="ask">
          <input
            value={question}
            onChange={(e) =>
              setQuestion(
                e.target.value
              )
            }
            placeholder="e.g. show attrition by department"
            onKeyDown={(e) => {
              if (
                e.key ===
                'Enter'
              ) {
                ask();
              }
            }}
          />

          <button
            className="primary"
            onClick={ask}
          >
            Ask
          </button>
        </div>

        {answer && (
          <pre className="data-box">
            {JSON.stringify(
              answer,
              null,
              2
            )}
          </pre>
        )}
      </div>

    </main>
  );
}

/* =========================================================
   FORECAST COMPONENT
   ========================================================= */

function Forecast({
  token,
}: {
  token: string;
}) {
  const [d, setD] =
    useState<AnyMap | null>(
      null
    );

  useEffect(() => {
    if (!token) {
      return;
    }

    api(
      '/api/analytics/forecast/headcount',
      token
    )
      .then(setD)
      .catch(
        (e) =>
          console.error(
            'Forecast error:',
            e
          )
      );
  }, [token]);

  return (
    <div className="card">
      <h2>
        6-month headcount forecast
      </h2>

      <table className="table">
        <thead>
          <tr>
            <th>
              Month
            </th>

            <th>
              Predicted
            </th>

            <th>
              Range
            </th>
          </tr>
        </thead>

        <tbody>
          {(d?.predicted ||
            []
          ).map(
            (x: any) => (
              <tr
                key={
                  x.month
                }
              >
                <td>
                  {x.month}
                </td>

                <td>
                  {x.predicted}
                </td>

                <td>
                  {x.lower} –{' '}
                  {x.upper}
                </td>
              </tr>
            )
          )}
        </tbody>
      </table>
    </div>
  );
}

/* =========================================================
   PAY EQUITY COMPONENT
   ========================================================= */

function Equity({
  token,
}: {
  token: string;
}) {
  const [d, setD] =
    useState<AnyMap | null>(
      null
    );

  useEffect(() => {
    if (!token) {
      return;
    }

    api(
      '/api/analytics/pay-equity',
      token
    )
      .then(setD)
      .catch(
        (e) =>
          console.error(
            'Pay equity error:',
            e
          )
      );
  }, [token]);

  return (
    <div className="card">
      <h2>
        Pay equity
      </h2>

      <pre className="data-box">
        {d
          ? JSON.stringify(
              d,
              null,
              2
            )
          : 'Loading…'}
      </pre>
    </div>
  );
}

/* =========================================================
   REPORTS COMPONENT
   ========================================================= */

function Reports({
  token,
}: {
  token: string;
}) {
  const [fields, setFields] =
    useState(
      'id,name,department,designation,salary,status'
    );

  const [out, setOut] =
    useState<AnyMap | null>(
      null
    );

  const run = async () => {
    try {
      setOut(
        await api(
          '/api/analytics/reports/custom',
          token,
          {
            method: 'POST',

            body: JSON.stringify({
              name: 'Lens UI report',

              fields: fields
                .split(',')
                .map(
                  (x) =>
                    x.trim()
                )
                .filter(
                  Boolean
                ),

              format: 'JSON',

              filters: [],

              group_by: [],
            }),
          }
        )
      );
    } catch (e: any) {
      setOut({
        error:
          e.message,
      });
    }
  };

  return (
    <div className="card">
      <h2>
        Custom report builder
      </h2>

      <p className="muted">
        Allowed fields:
      </p>

      <p className="muted">
        id, name, email, department,
        designation, salary, status,
        location, employmentType,
        grade, gender, joiningDate,
        separationDate,
        separationReason
      </p>

      <input
        value={fields}
        onChange={(e) =>
          setFields(
            e.target.value
          )
        }
        style={{
          width: '100%',
          padding: 10,
        }}
      />

      <button
        className="primary"
        onClick={run}
        style={{
          marginTop: 10,
        }}
      >
        Generate
      </button>

      {out && (
        <pre className="data-box">
          {JSON.stringify(
            out,
            null,
            2
          )}
        </pre>
      )}
    </div>
  );
}
